package com.saltechdigital.dliver.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class AddressDeserializer implements JsonDeserializer<Address> {
    @Override
    public Address deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Address address = new Address();

        JsonObject jsonObject = json.getAsJsonObject();
        /*address.setDefaut(jsonObject.get("defaut").getAsBoolean());*/
        /*if (jsonObject.has("defaut")){

        }*/
        return address;
    }
}
