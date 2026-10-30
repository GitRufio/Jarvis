package pl.rufio.Tools.packageWithTools;


import pl.rufio.Tools.Tool;

public class GetOsName implements Tool {
    @Override
    public String getName() {
        return "system_info";
    }

    @Override
    public String getDescription() {
        return "Returns information about the system";
    }

    @Override
    public String getParametersSchema() {
        return """
                {
                    "type": "object",
                    "properties": {},
                    "required": []
                }
                """;
    }

    @Override
    public String execute(String arguments) throws Exception {
        return System.getProperty("os.name");
    }
}
