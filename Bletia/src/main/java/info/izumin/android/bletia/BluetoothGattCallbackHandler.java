package info.izumin.android.bletia;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

import info.izumin.android.bletia.wrapper.BluetoothGattCallbackWrapper;
import info.izumin.android.bletia.wrapper.BluetoothGattWrapper;

/**
 * Created by izumin on 9/7/15.
 */
public class BluetoothGattCallbackHandler extends BluetoothGattCallbackWrapper {

    private Callback mCallback;
    private BleEventStore mEventStore;

    public BluetoothGattCallbackHandler(Callback callback, BleEventStore eventStore) {
        mCallback = callback;
        mEventStore = eventStore;
    }

    @Override
    public void onConnectionStateChange(BluetoothGattWrapper gatt, int status, int newState) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                mCallback.onConnect(gatt);
            }
            if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                mCallback.onDisconnect(gatt);
            }
        } else {
            mCallback.onError(BleStatus.valueOf(status));
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGattWrapper gatt, int status) {
        // TODO: Not yet implemented.
    }

    @Override
    public void onCharacteristicRead(BluetoothGattWrapper gatt, BluetoothGattCharacteristic characteristic, int status) {
        handleBleEvent(BleEvent.Type.READ_CHARACTERISTIC, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGattWrapper gatt, BluetoothGattCharacteristic characteristic, int status) {
        handleBleEvent(BleEvent.Type.WRITE_CHARACTERISTIC, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGattWrapper gatt, BluetoothGattCharacteristic characteristic) {
        // TODO: Not yet implemented.
    }

    @Override
    public void onDescriptorRead(BluetoothGattWrapper gatt, BluetoothGattDescriptor descriptor, int status) {
        handleBleEvent(BleEvent.Type.READ_DESCRIPTOR, descriptor, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGattWrapper gatt, BluetoothGattDescriptor descriptor, int status) {
        handleBleEvent(BleEvent.Type.WRITE_DESCRIPTOR, descriptor, status);
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGattWrapper gatt, int status) {
        // TODO: Not yet implemented.
    }

    @Override
    public void onReadRemoteRssi(BluetoothGattWrapper gatt, int rssi, int status) {
        // TODO: Not yet implemented.
    }

    @Override
    public void onMtuChanged(BluetoothGattWrapper gatt, int mtu, int status) {
        // TODO: Not yet implemented.
    }

    private void handleBleEvent(BleEvent.Type type, BluetoothGattCharacteristic characteristic, int status) {
        handleBleEvent(mEventStore.closeEvent(type, characteristic.getUuid()), status);
    }

    private void handleBleEvent(BleEvent.Type type, BluetoothGattDescriptor descriptor, int status) {
        handleBleEvent(mEventStore.closeEvent(type, descriptor.getUuid()), status);
    }

    private <T> void handleBleEvent(BleEvent<T> event, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            event.getDeferred().resolve(event.getValue());
        } else {
            event.getDeferred().reject(BleStatus.valueOf(status));
        }
    }

    interface Callback {
        void onConnect(BluetoothGattWrapper gatt);
        void onDisconnect(BluetoothGattWrapper gatt);
        void onError(BleStatus status);
    }
}
