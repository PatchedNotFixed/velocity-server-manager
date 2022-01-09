package ch.fetz.ServerManager.Utils;

public enum SQLStatementParameterType {
    STRING(0),
    INT(1),
    DOUBLE(2),
    BOOL(3),
    LONG(4);

    private final int identifier;

    SQLStatementParameterType(int identifier) {
        this.identifier = identifier;
    }

    public int getIdentifier() {
        return identifier;
    }

    public static SQLStatementParameterType fromIdentifier(int identifier){
        for(SQLStatementParameterType type : SQLStatementParameterType.values()){
            if(type.identifier == identifier){
                return type;
            }
        }
        return SQLStatementParameterType.STRING;
    }
}
