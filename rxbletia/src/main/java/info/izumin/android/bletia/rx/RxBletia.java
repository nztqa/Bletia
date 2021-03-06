package info.izumin.android.bletia.rx;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import info.izumin.android.bletia.core.AbstractBletia;
import info.izumin.android.bletia.core.BletiaException;
import info.izumin.android.bletia.rx.action.RxConnectAction;
import info.izumin.android.bletia.rx.action.RxDisableNotificationAction;
import info.izumin.android.bletia.rx.action.RxDisconnectAction;
import info.izumin.android.bletia.rx.action.RxDiscoverServicesAction;
import info.izumin.android.bletia.rx.action.RxEnableNotificationAction;
import info.izumin.android.bletia.rx.action.RxReadCharacteristicAction;
import info.izumin.android.bletia.rx.action.RxReadDescriptorAction;
import info.izumin.android.bletia.rx.action.RxReadRemoteRssiAction;
import info.izumin.android.bletia.rx.action.RxWriteCharacteristicAction;
import info.izumin.android.bletia.rx.action.RxWriteDescriptorAction;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by izumin on 11/15/15.
 */
public class RxBletia extends AbstractBletia {
    public static final String TAG = RxBletia.class.getSimpleName();

    private final Map<UUID, RxEnableNotificationAction> mEnabledNotificationList;
    private final PublishSubject<BletiaException> mExceptionSubject;

    public RxBletia(Context context) {
        super(context);
        setSubListener(mListener);
        mEnabledNotificationList = new HashMap<>();
        mExceptionSubject = PublishSubject.create();
    }

    public Observable<Void> connect(BluetoothDevice device) {
        return execute(new RxConnectAction(device, getStateContainer()));
    }

    public Observable<Void> disconnect() {
        return execute(new RxDisconnectAction(getStateContainer()));
    }

    public Observable<Void> discoverServices() {
        return execute(new RxDiscoverServicesAction(getStateContainer()));
    }

    public Observable<BluetoothGattCharacteristic> writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        return execute(new RxWriteCharacteristicAction(characteristic));
    }

    public Observable<BluetoothGattCharacteristic> readCharacteristic(BluetoothGattCharacteristic characteristic) {
        return execute(new RxReadCharacteristicAction(characteristic));
    }

    public Observable<BluetoothGattDescriptor> writeDescriptor(BluetoothGattDescriptor descriptor) {
        return execute(new RxWriteDescriptorAction(descriptor));
    }

    public Observable<BluetoothGattDescriptor> readDescriptor(BluetoothGattDescriptor descriptor) {
        return execute(new RxReadDescriptorAction(descriptor));
    }

    public Observable<BluetoothGattCharacteristic> enableNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        return enabled ? enableNotification(characteristic) : disableNotification(characteristic);
    }

    public Observable<BluetoothGattCharacteristic> enableNotification(final BluetoothGattCharacteristic characteristic) {
        final UUID uuid = characteristic.getUuid();
        if (mEnabledNotificationList.containsKey(uuid)) {
            return mEnabledNotificationList.get(uuid).getResolver();
        }
        final RxEnableNotificationAction action = new RxEnableNotificationAction(characteristic);
        mEnabledNotificationList.put(characteristic.getUuid(), action);
        return execute(action).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                mEnabledNotificationList.remove(uuid);
            }
        });
    }

    public Observable<BluetoothGattCharacteristic> disableNotification(BluetoothGattCharacteristic characteristic) {
        return execute(new RxDisableNotificationAction(characteristic))
                .doOnNext(new Action1<BluetoothGattCharacteristic>() {
                    @Override
                    public void call(BluetoothGattCharacteristic characteristic) {
                        mEnabledNotificationList.remove(characteristic.getUuid());
                    }
                });
    }

    public Observable<Integer> readRemoteRssi() {
        return execute(new RxReadRemoteRssiAction());
    }

    public Observable<BletiaException> observeError() {
        return mExceptionSubject;
    }

    private final BleEventListener mListener = new BleEventListener() {
        @Override
        public void onCharacteristicChanged(final BluetoothGattCharacteristic characteristic) {
            final UUID uuid = characteristic.getUuid();
            if (mEnabledNotificationList.containsKey(uuid)) {
                mEnabledNotificationList.get(uuid).resolve(characteristic);
            }
        }

        @Override
        public void onError(BletiaException exception) {
            mExceptionSubject.onNext(exception);
        }
    };
}
