package info.izumin.android.bletia.core;

import android.os.Message;

import java.util.UUID;

import info.izumin.android.bletia.core.action.AbstractAction;
import info.izumin.android.bletia.core.action.AbstractConnectAction;
import info.izumin.android.bletia.core.action.AbstractDisconnectAction;
import info.izumin.android.bletia.core.action.AbstractDiscoverServicesAction;
import info.izumin.android.bletia.core.action.AbstractEnableNotificationAction;
import info.izumin.android.bletia.core.action.AbstractReadCharacteristicAction;
import info.izumin.android.bletia.core.action.AbstractReadDescriptorAction;
import info.izumin.android.bletia.core.action.AbstractReadRemoteRssiAction;
import info.izumin.android.bletia.core.action.AbstractWriteCharacteristicAction;
import info.izumin.android.bletia.core.action.AbstractWriteDescriptorAction;

/**
 * Created by izumin on 10/3/15.
 */
enum ActionMessageHandler {
    READ_CHARACTERISTIC(AbstractAction.Type.READ_CHARACTERISTIC) {
        @Override
        public boolean execute(Message msg, StateContainer container) {
            return getQueueFromContainer(container).execute(getIdentityFromMessage(msg), container.getGattWrapper());
        }

        @Override
        public boolean isRunning(Message msg, StateContainer container) {
            return getQueueFromContainer(container).isRunning(getIdentityFromMessage(msg));
        }

        private UUID getIdentityFromMessage(Message msg) {
            return (UUID) msg.getData().getSerializable(AbstractAction.KEY_IDENTITY);
        }

        private ActionQueue<AbstractReadCharacteristicAction<?>, UUID> getQueueFromContainer(StateContainer container) {
            return container.getReadCharacteristicActionQueue();
        }
    },
    WRITE_CHARACTERISTIC(AbstractAction.Type.WRITE_CHARACTERISTIC) {
        @Override
        public boolean execute(Message msg, StateContainer container) {
            return getQueueFromContainer(container).execute(getIdentityFromMessage(msg), container.getGattWrapper());
        }

        @Override
        public boolean isRunning(Message msg, StateContainer container) {
            return getQueueFromContainer(container).isRunning(getIdentityFromMessage(msg));
        }

        private UUID getIdentityFromMessage(Message msg) {
            return (UUID) msg.getData().getSerializable(AbstractAction.KEY_IDENTITY);
        }

        private ActionQueue<AbstractWriteCharacteristicAction<?>, UUID> getQueueFromContainer(StateContainer container) {
            return container.getWriteCharacteristicActionQueue();
        }
    },
    READ_DESCRIPTOR(AbstractAction.Type.READ_DESCRIPTOR) {
        @Override
        public boolean execute(Message msg, StateContainer container) {
            return getQueueFromContainer(container).execute(getIdentityFromMessage(msg), container.getGattWrapper());
        }

        @Override
        public boolean isRunning(Message msg, StateContainer container) {
            return getQueueFromContainer(container).isRunning(getIdentityFromMessage(msg));
        }

        private UUID getIdentityFromMessage(Message msg) {
            return (UUID) msg.getData().getSerializable(AbstractAction.KEY_IDENTITY);
        }

        private ActionQueue<AbstractReadDescriptorAction<?>, UUID> getQueueFromContainer(StateContainer container) {
            return container.getReadDescriptorActionQueue();
        }
    },
    WRITE_DESCRIPTOR(AbstractAction.Type.WRITE_DESCRIPTOR) {
        @Override
        public boolean execute(Message msg, StateContainer container) {
            return getQueueFromContainer(container).execute(getIdentityFromMessage(msg), container.getGattWrapper());
        }

        @Override
        public boolean isRunning(Message msg, StateContainer container) {
            return getQueueFromContainer(container).isRunning(getIdentityFromMessage(msg));
        }

        private UUID getIdentityFromMessage(Message msg) {
            return (UUID) msg.getData().getSerializable(AbstractAction.KEY_IDENTITY);
        }

        private ActionQueue<AbstractWriteDescriptorAction<?>, UUID> getQueueFromContainer(StateContainer container) {
            return container.getWriteDescriptorActionQueue();
        }
    },
    ENABLE_NOTIFICATION(AbstractAction.Type.ENABLE_NOTIFICATION) {
        @Override
        public boolean execute(Message msg, StateContainer container) {
            return getQueueFromContainer(container).execute(getIdentityFromMessage(msg), container.getGattWrapper());
        }

        @Override
        public boolean isRunning(Message msg, StateContainer container) {
            return getQueueFromContainer(container).isRunning(getIdentityFromMessage(msg));
        }

        private UUID getIdentityFromMessage(Message msg) {
            return (UUID) msg.getData().getSerializable(AbstractAction.KEY_IDENTITY);
        }

        private ActionQueue<AbstractEnableNotificationAction<?>, UUID> getQueueFromContainer(StateContainer container) {
            return container.getEnableNotificationActionQueue();
        }
    },
    READ_REMOTE_RSSI(AbstractAction.Type.READ_REMOTE_RSSI) {
        @Override
        public boolean execute(Message msg, StateContainer container) {
            return getQueueFromContainer(container).execute(null, container.getGattWrapper());
        }

        @Override
        public boolean isRunning(Message msg, StateContainer container) {
            return getQueueFromContainer(container).isRunning(null);
        }

        private ActionQueue<AbstractReadRemoteRssiAction<?>, Void> getQueueFromContainer(StateContainer container) {
            return container.getReadRemoteRssiActionQueue();
        }
    },
    CONNECT(AbstractAction.Type.CONNECT) {
        @Override
        public boolean execute(Message msg, StateContainer container) {
            return getQueueFromContainer(container).execute(null, container.getGattWrapper());
        }

        @Override
        public boolean isRunning(Message msg, StateContainer container) {
            return getQueueFromContainer(container).isRunning(null);
        }

        private ActionQueue<AbstractConnectAction<?>, Void> getQueueFromContainer(StateContainer container) {
            return container.getConnectActionQueue();
        }
    },
    DISCOVER_SERVICES(AbstractAction.Type.DISCOVER_SERVICES) {
        @Override
        public boolean execute(Message msg, StateContainer container) {
            return getQueueFromContainer(container).execute(null, container.getGattWrapper());
        }

        @Override
        public boolean isRunning(Message msg, StateContainer container) {
            return getQueueFromContainer(container).isRunning(null);
        }

        private ActionQueue<AbstractDiscoverServicesAction<?>, Void> getQueueFromContainer(StateContainer container) {
            return container.getDiscoverServicesActionQueue();
        }
    },
    DISCONNECT(AbstractAction.Type.DISCONNECT) {
        @Override
        public boolean execute(Message msg, StateContainer container) {
            return getQueueFromContainer(container).execute(null, container.getGattWrapper());
        }

        @Override
        public boolean isRunning(Message msg, StateContainer container) {
            return getQueueFromContainer(container).isRunning(null);
        }

        private ActionQueue<AbstractDisconnectAction<?>, Void> getQueueFromContainer(StateContainer container) {
            return container.getDisconnectActionQueue();
        }
    };

    private final AbstractAction.Type mType;

    ActionMessageHandler(AbstractAction.Type type) {
        mType = type;
    }

    public AbstractAction.Type getType() {
        return mType;
    }

    public static ActionMessageHandler valueOf(AbstractAction.Type type) {
        return valueOf(type.getCode());
    }

    public static ActionMessageHandler valueOf(int what) {
        for (ActionMessageHandler handler : values()) {
            if (what == handler.getType().getCode()) { return handler; }
        }
        throw new IllegalArgumentException();
    }

    public abstract boolean execute(Message msg, StateContainer container);
    public abstract boolean isRunning(Message msg, StateContainer container);
}
