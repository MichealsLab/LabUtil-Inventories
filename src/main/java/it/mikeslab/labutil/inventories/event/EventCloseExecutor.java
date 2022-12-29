package it.mikeslab.labutil.inventories.event;

import it.mikeslab.labutil.inventories.annotations.CloseEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.util.Consumer;

import java.lang.reflect.Method;
import java.util.*;

public class EventCloseExecutor {
    private final String name;
    private Map<Object, Method> methods;

    public EventCloseExecutor(String name) {
        this.name = name;
        register();
    }

    public Consumer<InventoryCloseEvent> loadCloseEvent() {
        return (e) -> {
            if (methods.isEmpty()) return;

            for (Map.Entry<Object, Method> entry : methods.entrySet()) {
                try {
                    Method method = entry.getValue();
                    Object instance = entry.getKey();
                    method.invoke(instance, e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        };
    }

    public EventCloseExecutor register() {
        Map<Object, String> involvedClasses = ExecutorUtil.instances;
        this.methods = getCloseEventMethods(involvedClasses);
        return this;
    }


    private Map<Object, Method> getCloseEventMethods(Map<Object, String> objects) {
        Map<Object, Method> methods = new HashMap<>();


        for(Map.Entry<Object, String> entry : objects.entrySet()) {
            if(!Objects.equals(entry.getValue(), name)) continue;

            Object instance = entry.getKey();

            Class<?> clazz = instance.getClass();
            Method[] classMethods = clazz.getMethods();

            for(Method method : classMethods) {
                if(method.isAnnotationPresent(CloseEvent.class)) {
                    methods.put(instance, method);
                }
            }
        }
        return methods;
    }
}