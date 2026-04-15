"use client";

import { LoginSchema, LoginSchemaType } from "@/validations/LoginSchema";
import { yupResolver } from "@hookform/resolvers/yup";
import { useForm } from "react-hook-form";
import styles from "./loginForm.module.css";
import Link from "next/link";
import { LoginBodyRequest } from "@/interfaces/login/LoginBodyRequest";
import { loginService } from "@/services/loginService";

export const LoginForm: React.FC = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginSchemaType>({
    mode: "onChange",
    resolver: yupResolver(LoginSchema),
  });

  const onSubmit = async (data: LoginSchemaType) => {
    const body: LoginBodyRequest = {
      email: data.email,
      password: data.password,
    };

    try {
      await loginService(body);
    } catch (error) {
      console.error("Login failed:", error);
    }
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
            placeholder="Enter your email"
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
            placeholder="Enter your password"
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
