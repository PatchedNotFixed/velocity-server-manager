package de.gunnablescum.velocityservermanager.utils;

public class SQLStatementParameter {
    public SQLStatementParameterType type;
    public int index;
    public Object value;

    public SQLStatementParameter(SQLStatementParameterType type, int index, Object value) {
        this.type = type;
        this.index = index;
        this.value = value;
    }
}