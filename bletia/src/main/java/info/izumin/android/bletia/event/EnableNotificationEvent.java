package info.izumin.android.bletia.event;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import info.izumin.android.bletia.BleErrorType;
import info.izumin.android.bletia.BletiaException;
import info.izumin.android.bletia.util.NotificationUtils;
import info.izumin.android.bletia.wrapper.BluetoothGattWrapper;

/**
 * Created by izumin on 9/15/15.
 */
public class EnableNotificationEvent extends CharacteristicEvent {

    private final boolean mEnabled;

    public EnableNotificationEvent(BluetoothGattCharacteristic characteristic, boolean enabled) {
        super(characteristic);
        mEnabled = enabled;
    }

    @Override
    public Type getType() {
        return Type.ENABLE_NOTIFICATION;
    }

    @Override
    public void handle(BluetoothGattWrapper gattWrapper) {
        if (gattWrapper.setCharacteristicNotification(getCharacteristic(), mEnabled)) {
            BluetoothGattDescriptor descriptor = NotificationUtils.getDescriptor(getCharacteristic(), mEnabled);
            if (!gattWrapper.writeDescriptor(descriptor)) {
                getDeferred().reject(new BletiaException(BleErrorType.OPERATION_INITIATED_FAILURE, getCharacteristic(), descriptor));
            }
        } else {
            getDeferred().reject(new BletiaException(BleErrorType.REQUEST_FAILURE, getCharacteristic()));
        }
    }

    public boolean getEnabled() {
        return mEnabled;
    }
}
