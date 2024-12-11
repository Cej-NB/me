package run.entity;

public class Seed {
    private String id;
    private String sName;
    private String star;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    @Override
    public String toString() {
        return "Seed{" +
                "id='" + id + '\'' +
                ", sName='" + sName + '\'' +
                ", star='" + star + '\'' +
                '}';
    }
}
