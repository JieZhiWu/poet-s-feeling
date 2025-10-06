import ReactMarkdown from "react-markdown";
import "./index.css";

interface Props {
  value?: string;
  className?: string; // 确保有这个属性
}

/**
 * Markdown 查看器
 * @param props
 * @constructor
 */
const MdViewer: React.FC<Props> = (props) => {
  const { value, className = "" } = props;

  return (
    <div className={`md-viewer ${className}`}>
      <ReactMarkdown>{value || ""}</ReactMarkdown>
    </div>
  );
};

export default MdViewer;
