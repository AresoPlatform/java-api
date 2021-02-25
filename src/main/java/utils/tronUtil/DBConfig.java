

import lombok.Getter;
import lombok.Setter;
import org.json.Property;

import java.io.File;
import java.util.Map;
import java.util.Set;


public class DBConfig {
  @Setter
  public static boolean ENERGY_LIMIT_HARD_FORK = false;
  @Getter
  @Setter
  private static int dbVersion;
  @Getter
  @Setter
  private static String dbEngine;
  @Getter
  @Setter
  private static String outputDirectoryConfig;
  @Getter
  @Setter
  private static Map<String, Property> propertyMap;
  @Getter
  @Setter
  private static GenesisBlock genesisBlock;
  @Getter
  @Setter
  private static boolean dbSync;
  @Getter
  @Setter
  private static RocksDbSettings rocksDbSettings;
  @Getter
  @Setter
  private static int allowMultiSign;
  @Getter
  @Setter
  private static long maintenanceTimeInterval; // (ms)
  @Getter
  @Setter
  private static long allowAdaptiveEnergy; //committee parameter
  @Getter
  @Setter
  private static long allowDelegateResource; //committee parameter
  @Getter
  @Setter
  private static long allowTvmTransferTrc10; //committee parameter
  @Getter
  @Setter
  private static long allowTvmConstantinople; //committee parameter
  @Getter
  @Setter
  private static long allowTvmSolidity059; //committee parameter
  @Getter
  @Setter
  private static long forbidTransferToContract; //committee parameter
  @Getter
  @Setter
  private static long allowSameTokenName; //committee parameter
  @Getter
  @Setter
  private static long allowCreationOfContracts; //committee parameter
//  @Getter
//  @Setter
//  private static long allowShieldedTransaction; //committee parameter
  @Getter
  @Setter
  private static long allowShieldedTRC20Transaction;
  @Getter
  @Setter
  private static String Blocktimestamp;
  @Getter
  @Setter
  private static long allowAccountStateRoot;
  @Getter
  @Setter
  private static long blockNumForEneryLimit;
  @Getter
  @Setter
  private static long proposalExpireTime; // (ms)
  @Getter
  @Setter
  private static long allowProtoFilterNum;
  @Getter
  @Setter
  private static int checkFrozenTime; // for test only
  @Getter
  @Setter
  private static String dbDirectory;
  @Getter
  @Setter
  private static boolean fullNodeAllowShieldedTransaction;
  @Getter
  @Setter
  private static String zenTokenId;
  @Getter
  @Setter
  private static boolean vmTrace;
  @Getter
  @Setter
  private static boolean debug;
  @Getter
  @Setter
  private static double minTimeRatio;
  @Getter
  @Setter
  private static double maxTimeRatio;
  @Getter
  @Setter
  private static boolean solidityNode;
  @Getter
  @Setter
  private static int validContractProtoThreadNum;
  @Getter
  @Setter
  private static boolean supportConstant;
  @Getter
  @Setter
  private static int longRunningTime;
  @Getter
  @Setter
  private static long changedDelegation;

  @Getter
  @Setter
  private static Set<String> actuatorSet;

  @Getter
  @Setter
  private static boolean isECKeyCryptoEngine = true;

  public static boolean getEnergyLimitHardFork() {
    return ENERGY_LIMIT_HARD_FORK;
  }

  private static boolean hasProperty(String dbName) {
    if (propertyMap != null) {
      return propertyMap.containsKey(dbName);
    }
    return false;
  }

  private static Property getProperty(String dbName) {
    return propertyMap.get(dbName);
  }

  public static String getOutputDirectory() {
    if (!outputDirectoryConfig.equals("") && !outputDirectoryConfig.endsWith(File.separator)) {
      return outputDirectoryConfig + File.separator;
    }
    return outputDirectoryConfig;
  }
}
