"use server";

import { LoginBodyRequest } from "@/interfaces/login/LoginBodyRequest";
import { LoginResponse } from "@/interfaces/login/LoginResponse";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { COOKIES_KEYS } from "@/utils/cookiesKeys";
import { IBackendErrorResponse } from "@/interfaces/BackendErrorResponse";

export const loginService = async (body: LoginBodyRequest): Promise<void> => {
  const serverUrl = process.env.NEXT_PUBLIC_SERVER;
  const fetchUrl = `${serverUrl}/auth/login`;

  const response = await fetch(fetchUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });

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
