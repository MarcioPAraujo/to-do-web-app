"use server";

import { LoginBodyRequest } from "@/interfaces/login/LoginBodyRequest";
import { api } from "./api";
import { LoginResponse } from "@/interfaces/login/LoginResponse";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { COOKIES_KEYS } from "@/utils/cookiesKeys";

export const loginService = async (body: LoginBodyRequest): Promise<void> => {
  const endpoint = "/auth/login";

  const response = await api.post<LoginResponse>(endpoint, body);

  const { token } = response.data;

  const cookieStore = await cookies();

  cookieStore.set(COOKIES_KEYS.token, token, {
    httpOnly: true,
    sameSite: "lax",
    maxAge: 60 * 60 * 24, // 1 day
    expires: new Date(Date.now() + 60 * 60 * 24 * 1000), // 1 day
    path: "/",
  });

  redirect("/tasks");
};
