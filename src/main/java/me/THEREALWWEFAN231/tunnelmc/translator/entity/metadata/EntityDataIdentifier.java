package me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata;

import com.nukkitx.protocol.bedrock.data.entity.EntityData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityDataIdentifier {
    EntityData value();
}
