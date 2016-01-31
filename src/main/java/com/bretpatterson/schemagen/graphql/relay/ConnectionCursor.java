package com.bretpatterson.schemagen.graphql.relay;


public class ConnectionCursor {

    private String value;

	public ConnectionCursor() {
		setValue(null);
	}

    public ConnectionCursor(String value) {
        this.setValue(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {


        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionCursor that = (ConnectionCursor) o;

        if (getValue() != null ? !getValue().equals(that.getValue()) : that.getValue() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getValue() != null ? getValue().hashCode() : 0;
    }

    @Override
    public String toString() {
        return getValue();
    }

	public void setValue(String value) {
		this.value = value;
	}
}

