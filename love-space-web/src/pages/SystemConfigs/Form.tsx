import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { AxiosError } from "axios";
import PageMeta from "../../components/common/PageMeta";
import { useToast } from "../../context/ToastContext";
import { SystemConfigItem, getSystemConfig } from "../../api/systemConfigs";
import IosLlmPopupForm from "./IosLlmPopupForm";

/**
 * 系统配置编辑路由容器（/system-configs/:id/edit）：
 * 拉取配置详情后，按预置 key 分发渲染该配置项专属的 Form 页面组件。
 */
export default function SystemConfigForm() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const toast = useToast();
  const [config, setConfig] = useState<SystemConfigItem | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) {
      navigate("/system-configs", { replace: true });
      return;
    }
    setLoading(true);
    getSystemConfig(id)
      .then((data) => setConfig(data))
      .catch((err: AxiosError<{ detail?: string }>) => {
        toast.error(err.response?.data?.detail ?? "加载失败");
        navigate("/system-configs", { replace: true });
      })
      .finally(() => setLoading(false));
  }, [id, navigate, toast]);

  if (loading) {
    return (
      <div>
        <PageMeta title="编辑系统配置 | Love Space Admin" description="编辑系统配置" />
        <div className="text-gray-500">加载中...</div>
      </div>
    );
  }

  if (!config) return null;

  switch (config.key) {
    case "ios_llm_popup":
      return <IosLlmPopupForm config={config} />;
    default:
      return (
        <div>
          <PageMeta title="编辑系统配置 | Love Space Admin" description="编辑系统配置" />
          <div className="text-error-500 text-sm">
            暂未为配置项「{config.key}」定义专属编辑表单页面
          </div>
        </div>
      );
  }
}
