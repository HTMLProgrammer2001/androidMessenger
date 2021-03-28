package htmlprogrammer.labs.messanger.constants;

public enum CodeTypes {
    SIGNIN(1),
    LOGIN(2),
    CHANGE_PHONE(3);

    private int value;

    private CodeTypes(int val){
        value = val;
    }

    public int getValue(){
        return value;
    }
}
