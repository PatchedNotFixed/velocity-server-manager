package de.gunnablescum.velocityservermanager.utils;

public record SQLStatementParameter(SQLStatementParameterType type, int index, Object value) {
}