package com.loves.space.modules.ai.service;

import com.loves.ai.config.AiModelCatalog;
import com.loves.ai.generation.AiGenerator;
import com.loves.ai.generation.PingResult;
import com.loves.space.modules.ai.dto.AiConnectionTestResponse;
import com.loves.space.modules.ai.dto.AiSceneModelsRequest;
import com.loves.space.modules.ai.entity.AiScene;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** 模型列表、场景主备模型配置、连接测试。 */
@Service
public class AiModelService {

    private final AiModelCatalog catalog;
    private final AiGenerator generator;
    private final AiSceneService sceneService;

    public AiModelService(AiModelCatalog catalog, AiGenerator generator, AiSceneService sceneService) {
        this.catalog = catalog;
        this.generator = generator;
        this.sceneService = sceneService;
    }

    /** 可选模型（AI_MODELS）。 */
    public List<String> models() {
        return catalog.models();
    }

    /** 保存即生效：主模型必填且在白名单；备用可空，非空时须在白名单且不同于主模型；深度思考未传视为关闭。 */
    @Transactional
    public void setModels(UUID sceneId, AiSceneModelsRequest request) {
        String primary = request.primaryModel().strip();
        String backup = request.backupModel() == null || request.backupModel().isBlank() ? null : request.backupModel().strip();
        requireAllowed(primary, "主模型");
        if (backup != null) {
            requireAllowed(backup, "备用模型");
            if (backup.equals(primary)) {
                throw new IllegalArgumentException("备用模型不能与主模型相同");
            }
        }
        AiScene scene = sceneService.find(sceneId);
        scene.setPrimaryModel(primary);
        scene.setBackupModel(backup);
        scene.setThinkingEnabled(Boolean.TRUE.equals(request.thinkingEnabled()));
    }

    /** 发一条极短请求验证可达，不代表生成质量。 */
    public AiConnectionTestResponse testConnection(String model) {
        requireAllowed(model, "模型");
        PingResult result = generator.ping(model);
        return new AiConnectionTestResponse(result.ok(), result.latencyMs(), result.message());
    }

    private void requireAllowed(String model, String label) {
        if (!catalog.contains(model)) {
            throw new IllegalArgumentException(label + "不在可选模型列表中：" + model);
        }
    }
}
