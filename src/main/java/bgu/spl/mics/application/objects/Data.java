package bgu.spl.mics.application.objects;

import java.util.Objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    public Data(Type type, int size) {
        this.type = type;
        this.processed = 0;
        this.size = size;
    }

    public Type getType() {
        return type;
    }

    public int getProcessed() {
        return processed;
    }

    public int getSize() {
        return size;
    }

    public void increaseProcessed(int processed) {
        this.processed = this.processed+processed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return processed == data.processed &&
                size == data.size &&
                type == data.type;
    }

    @Override
    public String toString() {
        String typeS="Tabular";
        if(type==Type.Images)
            typeS="Image";
        else if(type==Type.Text)
            typeS="Text";
        return "{" +"\n" +
                "                        \"type\":\"" + typeS +"\"\n" +
                "                        \"size\":" + size +"\n" +
                "                    },"+"\n" ;
    }


    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;
}
