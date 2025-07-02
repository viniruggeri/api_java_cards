package fiap.tds.entities;

public class MyEntity {
    private int id;
    private String name;

    public MyEntity() {
    }

    public MyEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
