package pl.rufio.Tools;


import java.util.*;

public class ToolRegistry {

    private final Map<String, Tool> tools = new LinkedHashMap<>();

    public void register(Tool tool) {
        if(tools.containsKey(tool.getName())) {
            throw new IllegalArgumentException("Tool already registered: " + tool.getName());
        }
        tools.put(tool.getName(), tool);
    }
    public Optional<Tool> get(String name) {
        return Optional.ofNullable(tools.get(name));
    }

    public Collection<Tool> getAll(){
        return Collections.unmodifiableCollection(tools.values());
    }
}
