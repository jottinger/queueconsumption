package com.autumncode.examples.queueconsumption;


import com.google.common.base.MoreObjects;

public class Transition {
    String from;
    String to;

    public Transition() {
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("from", from)
                .add("to", to)
                .toString();
    }

    public Transition(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transition)) return false;
        Transition that = (Transition) o;
        return com.google.common.base.Objects.equal(getFrom(), that.getFrom()) &&
                com.google.common.base.Objects.equal(getTo(), that.getTo());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(getFrom(), getTo());
    }

    public String getFrom() {

        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
