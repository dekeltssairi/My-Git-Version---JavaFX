package logic;

public class UserName {
    private final String m_Name;

    public UserName(String i_Name) {
        m_Name = i_Name;
    }
    public UserName() {
        m_Name = "Administrator"; // Change to "Define" or const or what ever
    }

    public String GetName() {
        return m_Name;
    }

    @Override
    public String toString() {
        return m_Name;
    }
}
