package info.izumin.android.bletia.event;

import info.izumin.android.bletia.BleErrorType;
import info.izumin.android.bletia.BletiaException;
import info.izumin.android.bletia.wrapper.BluetoothGattWrapper;

/**
 * Created by izumin on 9/15/15.
 */
public class ReadRemoteRssiEvent extends Event<Integer> {
    @Override
    public Type getType() {
        return Type.READ_REMOTE_RSSI;
    }

    @Override
    public void handle(BluetoothGattWrapper gattWrapper) {
        if (!gattWrapper.readRemoteRssi()) {
            getDeferred().reject(new BletiaException(BleErrorType.REQUEST_FAILURE));
        }
    }
}
