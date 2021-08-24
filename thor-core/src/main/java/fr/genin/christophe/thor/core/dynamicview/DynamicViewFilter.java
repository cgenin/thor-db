package fr.genin.christophe.thor.core.dynamicview;

import java.io.Serializable;
import java.util.Objects;

public class DynamicViewFilter implements Serializable {
    private String type;
    private Object val;
    private String uid;


    public DynamicViewFilter copy() {
        return new DynamicViewFilter()
                .setUid(uid)
                .setVal(val)
                .setType(type);
    }

    public String getType() {
        return type;
    }

    public DynamicViewFilter setType(String type) {
        this.type = type;
        return this;
    }

    public Object getVal() {
        return val;
    }

    public DynamicViewFilter setVal(Object val) {
        this.val = val;
        return this;
    }


    public String getUid() {
        return uid;
    }

    public DynamicViewFilter setUid(String uid) {
        this.uid = uid;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicViewFilter that = (DynamicViewFilter) o;
        return Objects.equals(uid, that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }
}
