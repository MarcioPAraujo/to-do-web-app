"use server";

import { LoginBodyRequest } from "@/interfaces/login/LoginBodyRequest";
import { LoginResponse } from "@/interfaces/login/LoginResponse";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { COOKIES_KEYS } from "@/utils/cookiesKeys";
import { handleProxyRequest } from "@/app/api/proxy/[...slug]/route";
import { baseUrl } from "./api";
import { IBackendErrorResponse } from "@/interfaces/BackendErrorResponse";

export const loginService = async (body: LoginBodyRequest): Promise<void> => {
  const endpoint = `${baseUrl}/auth/login`;

  const response = await handleProxyRequest(
    new Request(endpoint, {
      method: "POST",
      body: JSON.stringify(body),
    }),
    {
      params: Promise.resolve({ slug: ["auth", "login"] }),
    },
  );

  if (!response.ok) {
    const errorData: IBackendErrorResponse = await response.json();
    throw new Error(errorData.message);
  }

  const data: LoginResponse = await response.json();

  const { token } = data;

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
