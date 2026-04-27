package org.canoestudios.mobsiege.config.props;

import com.google.gson.*;
import org.canoestudios.mobsiege.config.ConfigProperty;
import org.canoestudios.mobsiege.config.JsonHelper;

public class ConfigPropNumber extends ConfigProperty<Number>
{
    public ConfigPropNumber(String key, Number def) {
        super(key, def, json -> JsonHelper.getNumber(json, key), (json, val) -> json.addProperty(key, val));
    }
}
