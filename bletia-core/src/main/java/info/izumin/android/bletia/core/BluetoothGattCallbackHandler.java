package info.izumin.android.bletia.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import info.izumin.android.bletia.core.action.AbstractAction;
import info.izumin.android.bletia.core.util.NotificationUtils;
import info.izumin.android.bletia.core.wrapper.BluetoothGattCallbackWrapper;
import info.izumin.android.bletia.core.wrapper.BluetoothGattWrapper;

/**
 * Created by izumin on 9/7/15.
 */
public class BluetoothGattCallbackHandler extends BluetoothGattCallbackWrapper {

    private AbstractBletia.BleEventListener mListener;
    private StateContainer mContainer;

    public BluetoothGattCallbackHandler(AbstractBletia.BleEventListener listener, StateContainer queueContainer) {
        mListener = listener;
        mContainer = queueContainer;
    }

    @Override
    public void onConnectionStateChange(BluetoothGattWrapper gatt, int status, int newState) {
        if (newState == BluetoothGatt.STATE_DISCONNECTED) {
            mContainer.setState(BleState.DISCONNECTED);
            mContainer.getGattWrapper().close();
            mContainer.getMessageThread().stop();
            handleAction(mContainer.getDisconnectActionQueue(), null, null, status);
        } else if (newState == BluetoothGatt.STATE_CONNECTED) {
            mContainer.setState(BleState.CONNECTED);
            handleAction(mContainer.getConnectActionQueue(), null, null, status);
        } else {
            mListener.onError(new BletiaException(BleErrorType.valueOf(status)));
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGattWrapper gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            mContainer.setState(BleState.SERVICE_DISCOVERED);
        } else if (mContainer.getState() == BleState.SERVICE_DISCOVERING) {
            mContainer.setState(BleState.CONNECTED);
        }
        handleAction(mContainer.getDiscoverServicesActionQueue(), null, null, status);
    }

    @Override
    public void onCharacteristicRead(BluetoothGattWrapper gatt, BluetoothGattCharacteristic characteristic, int status) {
        handleAction(mContainer.getReadCharacteristicActionQueue(), characteristic, characteristic.getUuid(), status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGattWrapper gatt, BluetoothGattCharacteristic characteristic, int status) {
        handleAction(mContainer.getWriteCharacteristicActionQueue(), characteristic, characteristic.getUuid(), status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGattWrapper gatt, BluetoothGattCharacteristic characteristic) {
        mListener.onCharacteristicChanged(characteristic);
    }

    @Override
    public void onDescriptorRead(BluetoothGattWrapper gatt, BluetoothGattDescriptor descriptor, int status) {
        handleAction(mContainer.getReadDescriptorActionQueue(), descriptor, descriptor.getUuid(), status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGattWrapper gatt, BluetoothGattDescriptor descriptor, int status) {
        if (NotificationUtils.isEnabledNotificationDescriptor(descriptor)) {
            BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
            handleAction(mContainer.getEnableNotificationActionQueue(), characteristic, characteristic.getUuid(), status);
        } else {
            handleAction(mContainer.getWriteDescriptorActionQueue(), descriptor, descriptor.getUuid(), status);
        }
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGattWrapper gatt, int status) {
        // TODO: Not yet implemented.
    }

    @Override
    public void onReadRemoteRssi(BluetoothGattWrapper gatt, int rssi, int status) {
        handleAction(mContainer.getReadRemoteRssiActionQueue(), rssi, null, status);
    }

    @Override
    public void onMtuChanged(BluetoothGattWrapper gatt, int mtu, int status) {
        // TODO: Not yet implemented.
    }

    private <A extends AbstractAction<T, BletiaException, I, ?>, T, I> void handleAction(ActionQueue<A, I> queue, T result, I identity, int status) {
        A action = queue.dequeue(identity);
        if (action == null) {
            mListener.onError(new BletiaException(BleErrorType.valueOf(status)));
        } else if (status == BluetoothGatt.GATT_SUCCESS) {
            action.resolve(result);
        } else {
            action.reject(new BletiaException(action, BleErrorType.valueOf(status)));
        }
    }
}
