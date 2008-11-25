package smartrics.jmeter.builder;

import org.apache.jmeter.util.JMeterUtils;

public class Main {
    public static void main(String[] args) {
        JMeterUtils.setJMeterHome("/opt/java/jakarta-jmeter");
        Builder b = new Builder("testPlan", "threadGroup", 10, 0, 100);
        b.setDefaults("http", "localhost", "8080", "", null);
        b.addRestRequest("", "");
        String plan = b.build();
        System.out.println(plan);
    }
}
