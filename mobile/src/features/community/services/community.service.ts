
import { ENDPOINTS } from '@/constants/config';
import type { PageResponse } from '@/types/common';
import type { PublicDeckItem } from '../types';
import apiClient from '@/lib/api/axios';

/**
 * Pobiera publiczne talie (community)
 */
export async function getPublicDecks(params?: {
  page?: number;
  size?: number;
}): Promise<PageResponse<PublicDeckItem>> {
  const response = await apiClient.get<PageResponse<PublicDeckItem>>(ENDPOINTS.DECKS.PUBLIC, {
    params: {
      page: params?.page ?? 0,
      size: params?.size ?? 20,
    },
  });
  return response.data;
}
