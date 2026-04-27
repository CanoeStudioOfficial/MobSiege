package org.canoestudios.mobsiege.config.props;

import com.google.gson.*;
import org.canoestudios.mobsiege.config.ConfigProperty;
import org.canoestudios.mobsiege.config.JsonHelper;

public class ConfigPropBoolean extends ConfigProperty<Boolean>
{
    public ConfigPropBoolean(String name, boolean def) {
        super(name, def, json -> JsonHelper.getBoolean(json, name, false), (json, val) -> json.addProperty(name, val));
    }
}
