package pl.rufio.Tools;

public interface Tool {
    String getName();

    String getDescription();

    String getParametersSchema();

    String execute(String arguments) throws Exception;


}
