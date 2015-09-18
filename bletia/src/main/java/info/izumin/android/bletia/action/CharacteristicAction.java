package info.izumin.android.bletia.action;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import java.util.UUID;

/**
 * Created by izumin on 9/15/15.
 */
public abstract class CharacteristicAction extends Action<BluetoothGattCharacteristic> {

    private final BluetoothGattCharacteristic mCharacteristic;

    public CharacteristicAction(BluetoothGattCharacteristic characteristic) {
        super();
        mCharacteristic = characteristic;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return mCharacteristic;
    }

    @Override
    public UUID getUuid() {
        return mCharacteristic.getUuid();
    }

    @Override
    public Bundle getBundle() {
        Bundle bundle = super.getBundle();
        bundle.putSerializable(KEY_UUID, mCharacteristic.getUuid());
        return bundle;
    }
}