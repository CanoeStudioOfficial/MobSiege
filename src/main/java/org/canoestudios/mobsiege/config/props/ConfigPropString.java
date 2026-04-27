package org.canoestudios.mobsiege.config.props;

import com.google.gson.*;
import org.canoestudios.mobsiege.config.ConfigProperty;
import org.canoestudios.mobsiege.config.JsonHelper;

public class ConfigPropString extends ConfigProperty<String>
{
    public ConfigPropString(String key, String def) {
        super(key, def, json -> JsonHelper.getString(json, key), (json, val) -> json.addProperty(key, val));
    }
}
