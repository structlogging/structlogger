package cz.muni.fi;

public class Test {
    private final String ahoj;

    public Test(final String ahoj) {
        this.ahoj = ahoj;
    }

    public String getAhoj() {
        return ahoj;
    }

    @Override
    public String toString() {
        return "Test{" +
                "ahoj='" + ahoj + '\'' +
                '}';
    }
}
