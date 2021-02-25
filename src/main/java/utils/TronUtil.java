import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.util.DigestUtils;
import org.tron.TronWalletApi;
import org.tron.protos.Protocol;
import org.tron.wallet.crypto.ECKey;
import org.tron.wallet.util.ByteArray;
import org.tron.wallet.util.Sha256Hash;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;


public class TronUtil {

    public static BigDecimal getnowblock(){
        BigDecimal block = BigDecimal.valueOf(0);
        for (int i = 0; i < 3; i++) {
            if (block.compareTo(BigDecimal.valueOf(0))!=0){
                break;
            }
            String data = HttpUtil.doGet("");
            if (StringUtils.isNotEmpty(data)){
                try {
                    JSONObject object = JSON.parseObject(data).getJSONObject("block_header").getJSONObject("raw_data");
                    block = object.getBigDecimal("number");
                    break;
                }catch (Exception e){

                }
            }
        }
        return block;
    }

    public static BigDecimal readContract(String functionSelector, boolean isDivide) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contract_address", toHexAddress(""));
            jsonObject.put("function_selector", functionSelector);
            jsonObject.put("owner_address", toHexAddress(""));
            String transS = HttpUtil.doPostJson(PledgeConstant.tronNode + "/wallet/triggerconstantcontract", jsonObject.toJSONString());
            JSONObject trans = JSON.parseObject(transS);
            if (trans.getJSONObject("result").getBoolean("result")){
                BigInteger integer = Numeric.toBigInt(trans.getJSONArray("constant_result").getString(0));
                BigDecimal temp = new BigDecimal(integer);
                if (isDivide){
                    temp = temp.divide(PledgeConstant.decimals,6,BigDecimal.ROUND_DOWN);
                }
                return temp;
            }
        }catch (Exception e){
        }
        return null;
    }
    public static BigDecimal readLpContract(String functionSelector, boolean isDivide) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contract_address", toHexAddress(""));
            jsonObject.put("function_selector", functionSelector);
            jsonObject.put("owner_address", toHexAddress(PledgeConstant.figAddress));
            String transS = HttpUtil.doPostJson(PledgeConstant.tronNode + "/wallet/triggerconstantcontract", jsonObject.toJSONString());
            JSONObject trans = JSON.parseObject(transS);
            if (trans.getJSONObject("result").getBoolean("result")){
                BigInteger integer = Numeric.toBigInt(trans.getJSONArray("constant_result").getString(0));
                BigDecimal temp = new BigDecimal(integer);
                if (isDivide){
                    temp = temp.divide(PledgeConstant.decimals,6,BigDecimal.ROUND_DOWN);
                }
                return temp;
            }
        }catch (Exception e){
        }
        return null;
    }


    public static   Map<String,Object>  TrxTransation2(String toAddress, BigDecimal amount) {
        try {
            amount = amount.multiply(new BigDecimal("1000000"));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("to_address",toHexAddress(toAddress));
            jsonObject.put("owner_address", toHexAddress(""));
            jsonObject.put("amount", amount.toBigInteger());
            String transS = HttpUtil.doPostJson(PledgeConstant.tronNode + "/wallet/createtransaction", jsonObject.toJSONString());
            JSONObject trans = JSON.parseObject(transS);
            Protocol.Transaction transaction = Util.packTransaction2(trans.toJSONString());
            byte[] bytes = signTransaction2Byte(transaction.toByteArray(), ByteArray.fromHexString(""));
            String signTransation = ByteArray.toHexString(bytes);
            JSONObject jsonObjectGB = new JSONObject();
            jsonObjectGB.put("transaction", signTransation);
            String transationCompelets = HttpUtil.doPostJson(PledgeConstant.tronNode + "/wallet/broadcasthex", jsonObjectGB.toJSONString());
            JSONObject transationCompelet = JSON.parseObject(transationCompelets);
            String txid = transationCompelet.getString("txid");
            Map<String,Object> map = new HashMap<>();
            map.put("txId",txid);
            map.put("result",transationCompelet.getBoolean("result"));
            return  map;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String,Object> Trc20Transation2(String toAddress, BigDecimal amount) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contract_address", toHexAddress(PledgeConstant.contractAddress));
            jsonObject.put("function_selector", "transferOwner(address,uint256)");
            amount = amount.multiply(new BigDecimal("1000000"));
            BigInteger l = amount.toBigInteger();
            String parameter = encoderAbi(toHexAddress(toAddress).substring(2), l);
            jsonObject.put("parameter", parameter);
            jsonObject.put("owner_address", toHexAddress(""));
            jsonObject.put("call_value", 0);
            jsonObject.put("fee_limit", 20000000);
            String transS = HttpUtil.doPostJson(PledgeConstant.tronNode + "/wallet/triggersmartcontract", jsonObject.toJSONString());
            JSONObject trans = JSON.parseObject(transS);
            Protocol.Transaction transaction = Util.packTransaction(trans.getJSONObject("transaction").toJSONString(), false);
            byte[] bytes = signTransaction2Byte(transaction.toByteArray(), ByteArray.fromHexString(""));
            String signTransation = ByteArray.toHexString(bytes);
            JSONObject jsonObjectGB = new JSONObject();
            jsonObjectGB.put("transaction", signTransation);
            String transationCompelets = HttpUtil.doPostJson(PledgeConstant.tronNode + "/wallet/broadcasthex", jsonObjectGB.toJSONString());
            JSONObject transationCompelet = JSON.parseObject(transationCompelets);
            String txid = transationCompelet.getString("txid");
            Map<String,Object> map = new HashMap<>();
            map.put("txId",txid);
            map.put("result",transationCompelet.getBoolean("result"));
            return  map;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] signTransaction2Byte(byte[] transaction, byte[] privateKey)
            throws InvalidProtocolBufferException {
        ECKey ecKey = ECKey.fromPrivate(privateKey);
        Protocol.Transaction transaction1 = Protocol.Transaction.parseFrom(transaction);
        byte[] rawdata = transaction1.getRawData().toByteArray();
        byte[] hash = Sha256Hash.hash(rawdata);
        byte[] sign = ecKey.sign(hash).toByteArray();
        return transaction1.toBuilder().addSignature(ByteString.copyFrom(sign)).build().toByteArray();
    }

    public static String encoderAbi(String address, BigInteger amount) {
        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Address(address));
        inputParameters.add(new Uint256(amount));

        return FunctionEncoder.encodeConstructor(inputParameters);
    }

    public static String encoderAllAbi(String owner,String sender) {
        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Address(owner));
        inputParameters.add(new Address(sender));

        return FunctionEncoder.encodeConstructor(inputParameters);
    }

    public static String toHexAddress(String address) {
        return ByteArray.toHexString(TronWalletApi.decodeFromBase58Check(address));
    }

    public static boolean addressValid(String address) {
        return TronWalletApi.addressValid(address);
    }

    public static String DecodeAddress(String address){
        byte[] a = Hex.decode("41"+address.substring(2));
        ByteString b = ByteString.copyFrom(a);
        String h = Base58Check.bytesToBase58(b.toByteArray());
        return h;
    }

    public static String allowance(String owner, String sender) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contract_address", toHexAddress(PledgeConstant.ExchangeAddresss));
            jsonObject.put("function_selector", "allowance(address,address)");
            String parameter = encoderAllAbi(toHexAddress(owner).substring(2),toHexAddress(sender).substring(2));
            jsonObject.put("parameter", parameter);
            jsonObject.put("owner_address", toHexAddress(owner));
            String transS = HttpUtil.doPostJson( PledgeConstant.tronNode + "/wallet/triggerconstantcontract", jsonObject.toJSONString());
            JSONObject trans = JSON.parseObject(transS);
            if (trans.getJSONObject("result").getBoolean("result")){
                BigInteger amount = Numeric.toBigInt(trans.getJSONArray("constant_result").getString(0));
                return amount.toString();
            }
        }catch (Exception e){
        }
        return null;

    }

    public static List<Type> getAsoPrice() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contract_address", toHexAddress(PledgeConstant.valuesAggregatorAddress));
            jsonObject.put("function_selector", "getSingleInfo(address,address)");
            String parameter = encoderAllAbi(toHexAddress(PledgeConstant.figAddress).substring(2),toHexAddress(PledgeConstant.AsoTokenAddress).substring(2));
            jsonObject.put("parameter", parameter);
            jsonObject.put("owner_address", toHexAddress(""));
            String transS = HttpUtil.doPostJson( PledgeConstant.tronNode + "/wallet/triggerconstantcontract", jsonObject.toJSONString());
            JSONObject trans = JSON.parseObject(transS);
            if (trans.getJSONObject("result").getBoolean("result")){
                List<TypeReference<Type>> inputParameters = new ArrayList<>();
                inputParameters.add(new TypeReference<Type>() {
                    @Override
                    public java.lang.reflect.Type getType() {
                        return Address.class;
                    }
                });
                inputParameters.add(new TypeReference<Type>() {
                    @Override
                    public java.lang.reflect.Type getType() {
                        return Uint256.class;
                    }
                });
                inputParameters.add(new TypeReference<Type>() {
                    @Override
                    public java.lang.reflect.Type getType() {
                        return Uint256.class;
                    }
                });
                inputParameters.add(new TypeReference<Type>() {
                    @Override
                    public java.lang.reflect.Type getType() {
                        return Uint256.class;
                    }
                });
                inputParameters.add(new TypeReference<Type>() {
                    @Override
                    public java.lang.reflect.Type getType() {
                        return Uint256.class;
                    }
                });
                inputParameters.add(new TypeReference<Type>() {
                    @Override
                    public java.lang.reflect.Type getType() {
                        return Uint256.class;
                    }
                });
                inputParameters.add(new TypeReference<Type>() {
                    @Override
                    public java.lang.reflect.Type getType() {
                        return Uint256.class;
                    }
                });
                inputParameters.add(new TypeReference<Type>() {
                    @Override
                    public java.lang.reflect.Type getType() {
                        return Uint256.class;
                    }
                });
                List<Type> res = FunctionReturnDecoder.decode(trans.getJSONArray("constant_result").getString(0),inputParameters);
                return res;
            }
        }catch (Exception e){
        }
        return null;

    }


}
