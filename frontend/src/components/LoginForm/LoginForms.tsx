"use client";

import { LoginSchema, LoginSchemaType } from "@/validations/LoginSchema";
import { yupResolver } from "@hookform/resolvers/yup";
import { useForm } from "react-hook-form";
import styles from "./loginForm.module.css";
import Link from "next/link";

export const LoginForm: React.FC = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginSchemaType>({
    mode: "onChange",
    resolver: yupResolver(LoginSchema),
  });

  const onSubmit = (data: LoginSchemaType) => {
    console.log(data);
  };

  return (
    <div className={styles.formContainer}>
      <form onSubmit={handleSubmit(onSubmit)} className={styles.form}>
        <h1 className={styles.title}>Task list</h1>
        <div className={styles.field}>
          <label htmlFor="email" className={styles.label}>
            Email:
          </label>
          <input
            className={styles.input}
            id="email"
            type="text"
            {...register("email")}
          />
          {errors.email && (
            <p className={styles.errorMessage}>{errors.email.message}</p>
          )}
        </div>
        <div className={styles.field}>
          <label htmlFor="password" className={styles.label}>
            Password:
          </label>
          <input
            className={styles.input}
            id="password"
            type="password"
            {...register("password")}
          />
          {errors.password && (
            <p className={styles.errorMessage}>{errors.password.message}</p>
          )}
        </div>
        <button type="submit" className={styles.submitButton}>
          Login
        </button>
        <Link href="/register" className={styles.registerLink}>
          Sign in
        </Link>
      </form>
    </div>
  );
};
