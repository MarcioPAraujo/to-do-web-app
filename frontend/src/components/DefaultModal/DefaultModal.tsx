"use client";

import styles from "./defaultModal.module.css";

interface IButton {
  label: string;
  onClick: VoidFunction;
}

interface IDefaultModalProps {
  type: "success" | "error";
  isOpen: boolean;
  title: string;
  message: string;
  confirmButton: IButton;
  cancelButton?: IButton;
}
export const DefaultModal: React.FC<IDefaultModalProps> = ({
  confirmButton,
  isOpen,
  message,
  title,
  type,
  cancelButton,
}) => {
  if (!isOpen) return null;

  const confirm =
    type === "error" ? styles.confirmError : styles.confirmSuccess;
  const cancel = type === "error" ? styles.cancelError : styles.cancelSuccess;

  return (
    <div className={styles.background}>
      <div className={styles.content}>
        <h2 className={styles.title}>{title}</h2>
        <p className={styles.message}>{message}</p>
        <div className={styles.buttonsContainer}>
          {cancelButton && (
            <button
              type="button"
              onClick={cancelButton.onClick}
              className={`${styles.button} ${styles.cancelButton} ${cancel}`}
            >
              {cancelButton.label}
            </button>
          )}
          <button
            type="button"
            onClick={confirmButton.onClick}
            className={`${styles.button} ${styles.confirmButton} ${confirm}`}
          >
            {confirmButton.label}
          </button>
        </div>
      </div>
    </div>
  );
};
