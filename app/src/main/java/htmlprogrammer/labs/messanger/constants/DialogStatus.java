package htmlprogrammer.labs.messanger.constants;

public enum DialogStatus {
    MESSAGE(0),
    AUDIO(1),
    IMAGE(2),
    VIDEO(3),
    DOCUMENT(4);

    private int value;

    DialogStatus(int val){
        value = val;
    }

    public int getValue(){
        return value;
    }

    public static DialogStatus fromMessageType(MessageTypes type){
        DialogStatus status = null;

        switch (type){
            case IMAGE:
                status = DialogStatus.IMAGE;
                break;

            case VIDEO:
                status = DialogStatus.VIDEO;
                break;

            case DOCUMENT:
                status = DialogStatus.DOCUMENT;
                break;

            case AUDIO:
                status = DialogStatus.AUDIO;
                break;
        }

        return status;
    }
}
