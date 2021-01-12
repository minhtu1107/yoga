package com.tregix.serviceprovider.Utils;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
        // with the same object creation and query capabilities.
        // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
        // renamed.

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            RealmObjectSchema metaSchema = schema.get("Meta");

            if (metaSchema != null) {
                metaSchema
                        .addRealmListField("verifyUser", String.class);
                metaSchema
                        .addRealmListField("activeStatus", String.class);
                oldVersion++;
            }
        }
    }
}
