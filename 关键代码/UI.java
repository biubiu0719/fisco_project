package org.fisco.bcos.asset.client;

import java.util.*;

import java.io.*;

public class UI {
    private Map<String, String> map;//用户密码对应关系
    private Scanner scanner;
    private boolean status; //用户状态
    private String current; //当前登录用户
    private String user_info_path;
    public UI(){
        user_info_path = "info.txt";
        map = new HashMap<String, String>();
        scanner = new Scanner(System.in);
        status=false;
    }
    public String getCurrentUser(){
        return this.current;
    }

    public boolean getStatus(){
        return this.status;
    }
    //获取用户表
    public Map<String, String> getMap(){
        return this.map;
    }


    public void read_file(){
        try{
            FileReader fd = new FileReader(user_info_path);
            BufferedReader br = new BufferedReader(fd);
            String s1 = null;
            while((s1 = br.readLine()) != null) {
                String[] temp = s1.split("  ");
                map.put(temp[0],temp[1]);
            }
           br.close();
           fd.close();
        } catch (IOException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    public void write_file()
	{
		try{
            File file = new File(user_info_path);
            FileWriter fw = new FileWriter(file,false);
            for (String key : map.keySet()) {
                String temp = key+"  "+map.get(key);
                fw.write(temp+"\n");
            }
            fw.flush();
            fw.close();    

        } catch (IOException e) {
            System.out.println("Error:" + e.getMessage());
        }
	}


    public boolean login()
    {
        read_file();
        int select;
        String name,password,password_again;
        System.out.print("请输入数字：1）登录\t2）注册\n");
        // if(scanner.hasNextInt()){
            select = scanner.nextInt();
            if(select==1){

                    name=(String)scanner.nextLine();
                    System.out.print("请输入用户名: ");
                    name = (String)scanner.nextLine();
                    System.out.print("用户"+name+",请输入密码: ");
                    password= (String)scanner.nextLine();
                    if (map.get(name)!=null &&map.get(name).compareTo(password)==0){
                        current = name;
                        System.out.print("登录成功\n");
                        status=true;
                        return false;
                        
                    }
                    else{
                        System.out.print("登录失败\n");
                        status=false;
                        return false;
                    }
                }


                else if(select==2)
                {
                    name=(String)scanner.nextLine();
                    System.out.print("请输入注册的用户名: ");
                    name = (String)scanner.nextLine();
                    if(map.get(name)!=null){
                        System.out.print("用户名已经被使用过\n");
                        status=false;
                        return false;
                    }
                    else{
                        System.out.print("用户"+name+",请输入密码: ");
                        password= (String)scanner.nextLine();
                        System.out.print("请再一次输入密码: ");
                        password_again= (String)scanner.nextLine();
                        if(map.get(name)==null&&password.compareTo(password_again)==0){
                            map.put(name,password);
                            write_file();
                            read_file();
                            System.out.print("注册成功\n");
                            status=false;
                            return false;
                        }
                        else{
                            System.out.print("输入错误\n");
                            status=false;
                             return false;
                        }
                    }
                }

                    

            
        // }
        // else{
        //     System.out.println("输入错误\n");
        //     return false;
        // }
        return false;
    }

    public void clear(){
        for (int i = 0; i < 50; ++i) System.out.print("\n");
    }

    public void msg(){
        System.out.print("Dear "+current+", what do u want to do next?\n");
        System.out.println("0: 查询本人信用额度.\n1: 与某人交易（打欠条）\n2: 向银行贷款\n3: 支付欠条\n4: 查看欠条\n\n");

    }
}
