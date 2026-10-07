package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.model.dto.auth.LoginRequestDTO;

public interface IAuthService {
    String autenticarYGenerarToken(LoginRequestDTO dto);
}
