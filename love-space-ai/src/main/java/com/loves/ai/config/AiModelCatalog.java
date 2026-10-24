package com.loves.ai.config;

import java.util.List;

/** 后台可选模型目录，来源 AI_MODELS。 */
public class AiModelCatalog {

    private final AiProperties properties;

    public AiModelCatalog(AiProperties properties) {
        this.properties = properties;
    }

    /** 模型 ID 列表（已去空白、去空项，保持配置顺序）。 */
    public List<String> models() {
        return properties.models();
    }

    public boolean contains(String model) {
        return model != null && properties.models().contains(model);
    }
}
