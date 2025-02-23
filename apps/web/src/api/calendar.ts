import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export interface CalendarEvent {
  id: string;
  eventName: string;
  startTime: string;
  endTime: string;
  location?: string;
  description?: string;
}

export interface ImageUpload {
  id: string;
  originalFileName: string;
  s3Key: string;
  status: string;
  uploadedAt: string;
}

const api = axios.create({
  baseURL: API_URL,
  withCredentials: true,
});

export const uploadImage = async (file: File): Promise<ImageUpload> => {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await api.post<ImageUpload>('/api/images/upload', formData);
  return response.data;
};

export const processImage = async (imageId: string): Promise<CalendarEvent[]> => {
  const response = await api.post<CalendarEvent[]>(`/api/images/${imageId}/process`);
  return response.data;
};
