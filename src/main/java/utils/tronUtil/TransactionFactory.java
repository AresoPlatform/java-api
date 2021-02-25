

import com.google.protobuf.GeneratedMessageV3;
import org.tron.protos.Contract;
import org.tron.protos.Protocol.Transaction.Contract.ContractType;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionFactory {

  private static Map<ContractType, Class<? extends Actuator>> actuatorMap = new ConcurrentHashMap<>();
  private static Map<ContractType, Class<? extends GeneratedMessageV3>> contractMap = new ConcurrentHashMap<>();

  static {
    register(ContractType.CreateSmartContract, null, Contract.CreateSmartContract.class);
    register(ContractType.TriggerSmartContract, null, Contract.TriggerSmartContract.class);
  }

  public static void register(ContractType type, Class<? extends Actuator> actuatorClass,
                              Class<? extends GeneratedMessageV3> clazz) {
    Set<String> actuatorSet = DBConfig.getActuatorSet();
    if (actuatorClass != null && !actuatorSet.isEmpty() && !actuatorSet.contains(actuatorClass.getSimpleName())) {
      return;
    }

    if (type != null && actuatorClass != null) {
      actuatorMap.put(type, actuatorClass);
    }
    if (type != null && clazz != null) {
      contractMap.put(type, clazz);
    }
  }

  public static Class<? extends Actuator> getActuator(ContractType type) {
    return actuatorMap.get(type);
  }

  public static Class<? extends GeneratedMessageV3> getContract(ContractType type) {
    return contractMap.get(type);
  }

  public static Map<ContractType, Class<? extends Actuator>> getActuatorMap() {
    return actuatorMap;
  }

  public static Map<ContractType, Class<? extends GeneratedMessageV3>> getContractMap() {
    return contractMap;
  }
}
