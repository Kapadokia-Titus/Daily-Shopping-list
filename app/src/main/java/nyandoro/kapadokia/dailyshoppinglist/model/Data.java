package nyandoro.kapadokia.dailyshoppinglist.model;

public class Data  {
    String type;
    int ammount;
    String note;
    String date;
    String id;

    public Data(){

    }

    public Data(String type, int ammount, String note, String date, String id) {
        this.type = type;
        this.ammount = ammount;
        this.note = note;
        this.date = date;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmmount() {
        return ammount;
    }

    public void setAmmount(int ammount) {
        this.ammount = ammount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
