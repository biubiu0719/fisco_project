package org.fisco.bcos.asset.client;

import java.util.*;

import java.io.*;


import org.fisco.bcos.asset.client.UI;

import org.fisco.bcos.asset.contract.Asset.RegisterEventEventResponse;
import org.fisco.bcos.asset.contract.Asset.TransferEventEventResponse;
import org.fisco.bcos.asset.contract.Asset.AddTransactionEventEventResponse;
import org.fisco.bcos.asset.contract.Asset.UpdateTransactionEventEventResponse;
import org.fisco.bcos.asset.contract.Asset.SplitTransactionEventEventResponse;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;
import org.fisco.bcos.asset.contract.Asset;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class AssetClient {

  static Logger logger = LoggerFactory.getLogger(AssetClient.class);

  private BcosSDK bcosSDK;
  private Client client;
  private CryptoKeyPair cryptoKeyPair;

  public void initialize() throws Exception {
    @SuppressWarnings("resource")
    ApplicationContext context =
        new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    bcosSDK = context.getBean(BcosSDK.class);
    client = bcosSDK.getClient(1);
    cryptoKeyPair = client.getCryptoSuite().createKeyPair();
    client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
    logger.debug("create client for group1, account address is " + cryptoKeyPair.getAddress());
  }

  public void deployAssetAndRecordAddr() {

    try {
      Asset asset = Asset.deploy(client, cryptoKeyPair);
      System.out.println(
          " deploy Asset 成功, contract address is " + asset.getContractAddress());

      recordAssetAddr(asset.getContractAddress());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
      System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
    }
  }







  public void recordAssetAddr(String address) throws FileNotFoundException, IOException {
    Properties prop = new Properties();
    prop.setProperty("address", address);
    final Resource contractResource = new ClassPathResource("contract.properties");
    FileOutputStream fileOutputStream = new FileOutputStream(contractResource.getFile());
    prop.store(fileOutputStream, "contract address");
  }

  public String loadAssetAddr() throws Exception {
    // load Asset contact address from contract.properties
    Properties prop = new Properties();
    final Resource contractResource = new ClassPathResource("contract.properties");
    prop.load(contractResource.getInputStream());

    String contractAddress = prop.getProperty("address");
    if (contractAddress == null || contractAddress.trim().equals("")) {
      throw new Exception(" load Asset contract address failed, please deploy it first. ");
    }
    logger.info(" load Asset address from contract.properties, address is {}", contractAddress);
    return contractAddress;
  }
//判断是否存在该账户
  public boolean queryAssetAmount(String assetAccount) {
    try {
      String contractAddress = loadAssetAddr();
      Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
      Tuple2<BigInteger, BigInteger> result = asset.select(assetAccount);
      if (result.getValue1().compareTo(new BigInteger("0")) == 0) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());
      System.out.printf(" query asset account failed, error message is %s\n", e.getMessage());
      return false;
    }

  }
//查看账户余额
  public boolean queryAssetAmount2(String assetAccount) {
    try {
      String contractAddress = loadAssetAddr();
      Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
      Tuple2<BigInteger, BigInteger> result = asset.select(assetAccount);
      if (result.getValue1().compareTo(new BigInteger("0")) == 0) {
        System.out.printf(" 用户%s的额度为： %s \n", assetAccount, result.getValue2());
        return true;
      } else {
        System.out.printf("账户%s不存在\n", assetAccount);
        return false;
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
      logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());

      System.out.printf(" query asset account failed, error message is %s\n", e.getMessage());
      return false;
    }

  }

  public void registerAssetAccount(String assetAccount, BigInteger amount) {
    try {
      String contractAddress = loadAssetAddr();

      Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
      TransactionReceipt receipt = asset.register(assetAccount, amount);
      List<Asset.RegisterEventEventResponse> response = asset.getRegisterEventEvents(receipt);
      if (!response.isEmpty()) {
        if (response.get(0).ret.compareTo(new BigInteger("0")) == 0) {
          System.out.printf(
              " register asset account success => asset: %s, value: %s \n", assetAccount, amount);
        } else {
          System.out.printf(
              " register asset account failed, ret code is %s \n", response.get(0).ret.toString());
        }
      } else {
        System.out.println(" event log not found, maybe transaction not exec. ");
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();

      logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
      System.out.printf(" register asset account failed, error message is %s\n", e.getMessage());
    }
  }

  public void addAssetTransaction(String t_id, String acc1, String acc2, BigInteger money) {
		try {
			String contractAddress = loadAssetAddr();

			Asset asset = Asset.load(contractAddress, client,  cryptoKeyPair);
			TransactionReceipt receipt = asset.addTransaction(t_id, acc1, acc2, money);
			List<AddTransactionEventEventResponse> response = asset.getAddTransactionEventEvents(receipt);
			if (!response.isEmpty()) {
				if (response.get(0).ret.compareTo(new BigInteger("0")) == 0) {
					System.out.printf(" Add transaction success! id:" + t_id+"\n");
				} else {
					System.out.printf(" Add transaction failed, ret code is %s \n",
							response.get(0).ret.toString());
				}
			} else {
				System.out.println(" event log not found, maybe transaction not exec. ");
			}
		} catch (Exception e) {

			logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
			System.out.printf(" register asset account failed, error message is %s\n", e.getMessage());
		}
  }

  //更新欠条
  public void updateAssetTransaction(String t_id, BigInteger money) {
		try {
			String contractAddress = loadAssetAddr();
      Asset asset = Asset.load(contractAddress, client,  cryptoKeyPair);
			TransactionReceipt receipt = asset.updateTransaction(t_id, money);
			List<UpdateTransactionEventEventResponse> response = asset.getUpdateTransactionEventEvents(receipt);
			
			if (!response.isEmpty()) {
				if (response.get(0).ret.compareTo(new BigInteger("0")) == 0) {
					System.out.printf(" 成功支付欠条.\n" );
				} else {
					System.out.printf(" 支付失败\n",
							response.get(0).ret.toString());
				}
			} else {
				System.out.println(" event log not found, maybe transaction not exec. ");
			}
		} catch (Exception e) {

			logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
			System.out.printf(" register asset account failed, error message is %s\n", e.getMessage());
		}
  }
  public void queryAssetTransaction(String t_id) {
		try {
			String contractAddress = loadAssetAddr();

			Asset asset = Asset.load(contractAddress, client,  cryptoKeyPair);
			Tuple2<List<BigInteger>, List<byte[]>> result = asset.select_transaction(t_id);
			if (result.getValue1().get(0).compareTo(new BigInteger("0")) == 0) {
				String temp1 = new String(result.getValue2().get(0));
				String temp2 = new String(result.getValue2().get(1));
				System.out.printf("欠条\n ID: " + t_id + "; 债主: " + temp1 + "; 贷方: " + temp2 + "; 原始金额: " + result.getValue1().get(1) + "; 剩余待还: " + result.getValue1().get(2) + "\n");
			} else {
				System.out.printf("Transaction %s is not exist \n", t_id);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());

			System.out.printf(" query asset account failed, error message is %s\n", e.getMessage());
		}
	}



  public void transferAsset(String fromAssetAccount, String toAssetAccount, BigInteger amount) {
    try {
      String contractAddress = loadAssetAddr();
      Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
      TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount);
      List<Asset.TransferEventEventResponse> response = asset.getTransferEventEvents(receipt);
      if (!response.isEmpty()) {
        if (response.get(0).ret.compareTo(new BigInteger("0")) == 0) {
          System.out.printf(
              " transfer success => from_asset: %s, to_asset: %s, amount: %s \n",
              fromAssetAccount, toAssetAccount, amount);
        } else {
          System.out.printf(
              " transfer asset account failed, ret code is %s \n", response.get(0).ret.toString());
        }
      } else {
        System.out.println(" event log not found, maybe transaction not exec. ");
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();

      logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
      System.out.printf(" register asset account failed, error message is %s\n", e.getMessage());
    }
  }


  public static void main(String[] args) throws Exception {

    Scanner scanner;
    AssetClient client = new AssetClient();
    client.initialize();
    client.deployAssetAndRecordAddr();
    scanner = new Scanner(System.in);
    // System.out.println("欢迎进入Fisco-Bcos\n");
    // System.out.println("请输入数字：1）登录\t2）注册\n");
    // String acc;
    // acc = (String)scanner.nextLine();
    // System.out.printf("登录:%s\n",acc);
    
    UI test = new UI();
    // test.login();
    boolean flag=true;
    while(flag){
      while(test.login()==false){

        System.out.print("1\n");
        if(test.getStatus()==false){
          System.out.print("kkk\n");
          continue;
        }
        String x;
        Map<String,String> current_map = test.getMap();
        for(String key : current_map.keySet()){
          if(client.queryAssetAmount(key) == false){
            if(key.compareTo("bank")==0){
              x="100000000";
            }
            else{
              x="10000";
            }
            client.registerAssetAccount(key, new BigInteger(x));
            
          }
          
          client.queryAssetAmount2(key);

        }
        System.out.print("ttt\n");
        break;

      }
      test.clear();
      boolean flag_cmd=true;
      String waitkey,duifang;
      String money;
      int trans_id=9000;
      String trans_id_str;
      while(flag_cmd)
      {
        test.msg();
        int select_cmd;

        if(scanner.hasNextInt()){
          select_cmd=scanner.nextInt();
          switch(select_cmd){
            case 0:   //查询本人信用余额
              System.out.print("你的剩余信用为：\n");
              client.queryAssetAmount2(test.getCurrentUser());
              
              System.out.print("等待按钮.....");
              waitkey = (String)scanner.nextLine();
              waitkey = (String)scanner.nextLine();
              test.clear();
              break;

            case 1:   //与某人交易（打欠条）
              System.out.print("与某人交易（打欠条）\n");
              duifang=(String)scanner.nextLine();
              System.out.print("请选择对方: ");
              duifang=(String)scanner.nextLine();
              System.out.print("请输入金额: ");
              money = (String)scanner.nextLine();
              trans_id_str=trans_id+"";
              trans_id+=1;
              client.addAssetTransaction(trans_id_str, duifang, test.getCurrentUser(), new BigInteger(money));
              System.out.print("等待按钮.....");
              waitkey = (String)scanner.nextLine();
              waitkey = (String)scanner.nextLine();
              test.clear();
              break;
            

            case 2:   //向银行贷款
              System.out.print("向银行贷款\n");
              duifang=(String)scanner.nextLine();
              System.out.print("请输入金额: ");
              money = (String)scanner.nextLine();
              trans_id_str=trans_id+"";
              trans_id+=1;
              duifang="bank";
              client.addAssetTransaction(trans_id_str, duifang, test.getCurrentUser(), new BigInteger(money));
              System.out.print("等待按钮.....");
              waitkey = (String)scanner.nextLine();
              waitkey = (String)scanner.nextLine();
              test.clear();
              break;



            case 3:   //支付欠条
              System.out.print("支付欠条\n");
              duifang=(String)scanner.nextLine();
              System.out.print("请输入欠条ID: ");
              trans_id_str = (String)scanner.nextLine();
              System.out.print("请输入金额: ");
              money = (String)scanner.nextLine();
              client.updateAssetTransaction( trans_id_str, new BigInteger(money));
              System.out.print("等待按钮.....");
              waitkey = (String)scanner.nextLine();
              waitkey = (String)scanner.nextLine();
              test.clear();
              break;

            case 4:   //查看欠条
              System.out.print("查看欠条\n");
              duifang=(String)scanner.nextLine();
              System.out.print("请输入欠条ID: ");
              trans_id_str = (String)scanner.nextLine();
              client.queryAssetTransaction( trans_id_str);
              System.out.print("等待按钮.....");
              waitkey = (String)scanner.nextLine();
              waitkey = (String)scanner.nextLine();
              test.clear();
              break;
          }
        }
      }

    }

    System.exit(0);
  }
}
