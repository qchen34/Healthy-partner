package com.exp.sign;

import cn.bmob.v3.BmobObject;

public class Request extends BmobObject {

    private String to;
    private String from;
    private int status;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        return from != null ? from.equals(request.from) : request.from == null;
    }

    @Override
    public int hashCode() {
        return from != null ? from.hashCode() : 0;
    }
}
