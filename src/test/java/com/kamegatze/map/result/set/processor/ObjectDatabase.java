package com.kamegatze.map.result.set.processor;

import com.kamegatze.map.result.set.Column;

public final class ObjectDatabase {

    private String version;

    private String name;

    private String id;

    @Column("is_enable")
    private boolean isEnable;

    @Column("is_disable")
    private Boolean isDisable;

    public Boolean getDisable() {
        return isDisable;
    }

    public void setDisable(Boolean disable) {
        isDisable = disable;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
