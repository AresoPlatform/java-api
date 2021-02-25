

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public interface Actuator {

  boolean execute(Object result);

  boolean validate() ;

  ByteString getOwnerAddress() throws InvalidProtocolBufferException;

  long calcFee();

}
