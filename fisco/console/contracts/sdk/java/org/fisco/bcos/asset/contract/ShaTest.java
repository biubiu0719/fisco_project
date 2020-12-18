package org.fisco.bcos.asset.contract;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class ShaTest extends Contract {
    public static final String[] BINARY_ARRAY = {"60806040526040805190810160405280600e81526020017f48656c6c6f2c20536861546573740000000000000000000000000000000000008152506000908051906020019061004f929190610062565b5034801561005c57600080fd5b50610107565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a357805160ff19168380011785556100d1565b828001600101855582156100d1579182015b828111156100d05782518255916020019190600101906100b5565b5b5090506100de91906100e2565b5090565b61010491905b808211156101005760008160009055506001016100e8565b5090565b90565b6103db806101166000396000f300608060405260043610610057576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680633bc5de301461005c5780638b053758146100ec57806393730bbe14610171575b600080fd5b34801561006857600080fd5b506100716101f6565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100b1578082015181840152602081019050610096565b50505050905090810190601f1680156100de5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156100f857600080fd5b50610153600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610298565b60405180826000191660001916815260200191505060405180910390f35b34801561017d57600080fd5b506101d8600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610343565b60405180826000191660001916815260200191505060405180910390f35b606060008054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561028e5780601f106102635761010080835404028352916020019161028e565b820191906000526020600020905b81548152906001019060200180831161027157829003601f168201915b5050505050905090565b60006002826040518082805190602001908083835b6020831015156102d257805182526020820191506020810190506020830392506102ad565b6001836020036101000a0380198251168184511680821785525050505050509050019150506020604051808303816000865af1158015610316573d6000803e3d6000fd5b5050506040513d602081101561032b57600080fd5b81019080805190602001909291905050509050919050565b6000816040518082805190602001908083835b60208310151561037b5780518252602082019150602081019050602083039250610356565b6001836020036101000a038019825116818451168082178552505050505050905001915050604051809103902090509190505600a165627a7a72305820e7b7d5d8e916a92ceb776dd0dedf162af214621884a34d8bd69b878ba4aac0910029"};

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"60806040526040805190810160405280600e81526020017f48656c6c6f2c20536861546573740000000000000000000000000000000000008152506000908051906020019061004f929190610062565b5034801561005c57600080fd5b50610107565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a357805160ff19168380011785556100d1565b828001600101855582156100d1579182015b828111156100d05782518255916020019190600101906100b5565b5b5090506100de91906100e2565b5090565b61010491905b808211156101005760008160009055506001016100e8565b5090565b90565b6103db806101166000396000f300608060405260043610610057576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063a935d2841461005c578063d81443b4146100e1578063e211e0c114610166575b600080fd5b34801561006857600080fd5b506100c3600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506101f6565b60405180826000191660001916815260200191505060405180910390f35b3480156100ed57600080fd5b50610148600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506102a1565b60405180826000191660001916815260200191505060405180910390f35b34801561017257600080fd5b5061017b61030d565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101bb5780820151818401526020810190506101a0565b50505050905090810190601f1680156101e85780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b60006002826040518082805190602001908083835b602083101515610230578051825260208201915060208101905060208303925061020b565b6001836020036101000a0380198251168184511680821785525050505050509050019150506020604051808303816000865af1158015610274573d6000803e3d6000fd5b5050506040513d602081101561028957600080fd5b81019080805190602001909291905050509050919050565b6000816040518082805190602001908083835b6020831015156102d957805182526020820191506020810190506020830392506102b4565b6001836020036101000a03801982511681845116808217855250505050505090500191505060405180910390209050919050565b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103a55780601f1061037a576101008083540402835291602001916103a5565b820191906000526020600020905b81548152906001019060200180831161038857829003601f168201915b50505050509050905600a165627a7a72305820ce107714d506884b22c33682c0effd56c7e4f695caf69692ca495b39ace227150029"};

    public static final String SM_BINARY = String.join("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"constant\":true,\"inputs\":[],\"name\":\"getData\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_memory\",\"type\":\"bytes\"}],\"name\":\"getSha256\",\"outputs\":[{\"name\":\"result\",\"type\":\"bytes32\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_memory\",\"type\":\"bytes\"}],\"name\":\"getKeccak256\",\"outputs\":[{\"name\":\"result\",\"type\":\"bytes32\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final String FUNC_GETDATA = "getData";

    public static final String FUNC_GETSHA256 = "getSha256";

    public static final String FUNC_GETKECCAK256 = "getKeccak256";

    protected ShaTest(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public byte[] getData() throws ContractException {
        final Function function = new Function(FUNC_GETDATA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        return executeCallWithSingleValueReturn(function, byte[].class);
    }

    public TransactionReceipt getSha256(byte[] _memory) {
        final Function function = new Function(
                FUNC_GETSHA256, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.DynamicBytes(_memory)), 
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void getSha256(byte[] _memory, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_GETSHA256, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.DynamicBytes(_memory)), 
                Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForGetSha256(byte[] _memory) {
        final Function function = new Function(
                FUNC_GETSHA256, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.DynamicBytes(_memory)), 
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<byte[]> getGetSha256Input(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_GETSHA256, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<byte[]>(

                (byte[]) results.get(0).getValue()
                );
    }

    public Tuple1<byte[]> getGetSha256Output(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_GETSHA256, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<byte[]>(

                (byte[]) results.get(0).getValue()
                );
    }

    public TransactionReceipt getKeccak256(byte[] _memory) {
        final Function function = new Function(
                FUNC_GETKECCAK256, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.DynamicBytes(_memory)), 
                Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void getKeccak256(byte[] _memory, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_GETKECCAK256, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.DynamicBytes(_memory)), 
                Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForGetKeccak256(byte[] _memory) {
        final Function function = new Function(
                FUNC_GETKECCAK256, 
                Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.DynamicBytes(_memory)), 
                Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<byte[]> getGetKeccak256Input(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_GETKECCAK256, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<byte[]>(

                (byte[]) results.get(0).getValue()
                );
    }

    public Tuple1<byte[]> getGetKeccak256Output(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_GETKECCAK256, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<byte[]>(

                (byte[]) results.get(0).getValue()
                );
    }

    public static ShaTest load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new ShaTest(contractAddress, client, credential);
    }

    public static ShaTest deploy(Client client, CryptoKeyPair credential) throws ContractException {
        return deploy(ShaTest.class, client, credential, getBinary(client.getCryptoSuite()), "");
    }
}
