package com.example.CHdependency.services;

import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.dto.user.UserDeleteDTO;
import com.example.CHdependency.dto.user.UserPasswordDTO;
import com.example.CHdependency.dto.user.UserRequestDTO;
import com.example.CHdependency.dto.user.UserResponseDTO;
import com.example.CHdependency.entities.User;
import com.example.CHdependency.enums.user.Gender;
import com.example.CHdependency.mappers.UserMapper;
import com.example.CHdependency.repositories.UserRepository;
import com.example.CHdependency.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para UserServices, cobrindo cenários de criação, atualização de senha e exclusão de usuários.
 * Utiliza Mockito para isolamento das dependências e validação comportamental precisa.
 */
@ExtendWith(MockitoExtension.class)
class UserServicesTest {

    // === CONFIGURAÇÃO DE MOCKS E INJEÇÃO ===

    @InjectMocks
    private UserServices userServices; // Classe sob teste - recebe todos os mocks injetados automaticamente

    @Mock
    private UserRepository userRepository; // Simula operações de persistência

    @Mock
    private UserMapper userMapper; // Simula conversões entre DTOs e entidades

    @Mock
    private Utils utils; // Simula utilitários de validação

    @Mock
    private ConfigAuthentication config; // Simula configurações de autenticação

    @Mock
    private PasswordEncoder passwordEncoder; // Simula codificação e verificação de senhas

    // === OBJETOS DE TESTE COMPARTILHADOS ===

    private User user;
    private UserRequestDTO userRequestDTO;
    private UserDeleteDTO userDeleteDTO;
    private UserPasswordDTO userPasswordDTO;
    private UserResponseDTO userResponseDTO;

    /**
     * Configuração inicial executada antes de cada teste.
     * Prepara objetos de teste padronizados e configura stubs básicos para evitar exceções desnecessárias.
     */
    @BeforeEach
    void setUp() {
        // Configuração preventiva: evita UnnecessaryStubbingException em testes onde config.password() não é usado
        // O lenient() permite que este stub exista mesmo se não for chamado em todos os testes
        lenient().when(config.passwordEncoder()).thenReturn(passwordEncoder);

        // === CRIAÇÃO DE FIXTURES PADRÃO ===
        // Entidade User base para todos os testes
        user = new User();
        user.setEmail("marcodamasceno58@gmail.com");
        user.setPassword("MAPD95wd40_Z007@");
        user.setName("Marco");
        user.setGender(Gender.MALE);
        user.setAge(29);

        // DTO para requisições de criação de usuário
        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail(user.getEmail());
        userRequestDTO.setPassword(user.getPassword());
        userRequestDTO.setName(user.getName());
        userRequestDTO.setGender(user.getGender());
        userRequestDTO.setAge(user.getAge());

        // DTO para requisições de exclusão de usuário
        userDeleteDTO = new UserDeleteDTO();
        userDeleteDTO.setEmail(user.getEmail());
        userDeleteDTO.setPassword(user.getPassword());

        // DTO para requisições de alteração de senha
        userPasswordDTO = new UserPasswordDTO();
        userPasswordDTO.setEmail(user.getEmail());
        userPasswordDTO.setPassword(user.getPassword());
        userPasswordDTO.setNewPassword("NewPass123!");

        // DTO de resposta esperado
        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setGender(user.getGender());
        userResponseDTO.setAge(user.getAge());
    }

    /**
     * Testes para operação de criação de usuários.
     * Valida cenários de falha por dados inválidos e sucesso com dados corretos.
     */
    @Nested
    class Create {

        /**
         * CENÁRIO: Email inválido deve impedir criação do usuário
         * EXPECTATIVA: Retornar null e não persistir dados
         */
        @Test
        void shouldReturnNullIfEmailInvalid() {
            // Configura comportamento: mapeamento realizado, mas email é inválido
            when(userMapper.forUserEntity(userRequestDTO)).thenReturn(user);
            when(utils.validateEmail(user.getEmail())).thenReturn(false);

            // Executa operação
            UserResponseDTO result = userServices.create(userRequestDTO);

            // Verifica resultado e comportamento
            assertNull(result, "Deve retornar null quando email é inválido");

            // Garante que não houve tentativa de persistência
            verify(userRepository, never()).save(any());

            // Verifica fluxo de validação correto
            verify(userMapper).forUserEntity(userRequestDTO);
            verify(utils).validateEmail(user.getEmail());

            // Garante que não há interações extras ou dependências desnecessárias
            verifyNoMoreInteractions(userRepository, userMapper, utils);
            verifyNoInteractions(config, passwordEncoder);
        }

        /**
         * CENÁRIO: Senha inválida deve impedir criação do usuário
         * EXPECTATIVA: Retornar null após validação de email bem-sucedida
         */
        @Test
        void shouldReturnNullIfPasswordInvalid() {
            // Configura comportamento: email válido, mas senha inválida
            when(userMapper.forUserEntity(userRequestDTO)).thenReturn(user);
            when(utils.validateEmail(user.getEmail())).thenReturn(true);
            when(utils.validatePassword(user.getPassword())).thenReturn(false);

            // Executa operação
            UserResponseDTO result = userServices.create(userRequestDTO);

            // Verifica resultado
            assertNull(result, "Deve retornar null quando senha é inválida");

            // Garante que não houve persistência
            verify(userRepository, never()).save(any());

            // Verifica sequência de validações até o ponto de falha
            verify(userMapper).forUserEntity(userRequestDTO);
            verify(utils).validateEmail(user.getEmail());
            verify(utils).validatePassword(user.getPassword());

            // Confirma ausência de interações desnecessárias
            verifyNoMoreInteractions(userRepository, userMapper, utils);
            verifyNoInteractions(config, passwordEncoder);
        }

        /**
         * CENÁRIO: Nome inválido deve impedir criação do usuário
         * EXPECTATIVA: Retornar null após validações de email e senha bem-sucedidas
         */
        @Test
        void shouldReturnNullIfNameInvalid() {
            // Configura comportamento: email e senha válidos, mas nome inválido
            when(userMapper.forUserEntity(userRequestDTO)).thenReturn(user);
            when(utils.validateEmail(user.getEmail())).thenReturn(true);
            when(utils.validatePassword(user.getPassword())).thenReturn(true);
            when(utils.validateName(user.getName())).thenReturn(false);

            // Executa operação
            UserResponseDTO result = userServices.create(userRequestDTO);

            // Verifica resultado
            assertNull(result, "Deve retornar null quando nome é inválido");

            // Garante que não houve persistência
            verify(userRepository, never()).save(any());

            // Verifica toda a sequência de validações até o ponto de falha
            verify(userMapper).forUserEntity(userRequestDTO);
            verify(utils).validateEmail(user.getEmail());
            verify(utils).validatePassword(user.getPassword());
            verify(utils).validateName(user.getName());

            // Confirma ausência de interações com segurança
            verifyNoMoreInteractions(userRepository, userMapper, utils);
            verifyNoInteractions(config, passwordEncoder);
        }

        /**
         * CENÁRIO: Dados válidos devem resultar em criação bem-sucedida
         * EXPECTATIVA: Usuário criado, senha codificada e DTO de resposta retornado
         */
        @Test
        void shouldCreateUserWhenInputValid() {
            // Configura comportamento para fluxo de sucesso completo
            when(userMapper.forUserEntity(userRequestDTO)).thenReturn(user);
            when(utils.validateEmail(user.getEmail())).thenReturn(true);
            when(utils.validatePassword(user.getPassword())).thenReturn(true);
            when(utils.validateName(user.getName())).thenReturn(true);
            when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
            when(userMapper.forResponse(user)).thenReturn(userResponseDTO);

            // Executa operação
            UserResponseDTO result = userServices.create(userRequestDTO);

            // Verifica resultado positivo
            assertNotNull(result, "Deve retornar DTO de resposta quando dados são válidos");
            assertEquals(userResponseDTO, result, "DTO retornado deve corresponder ao esperado");
            assertEquals("encodedPassword", user.getPassword(), "Senha do usuário deve estar codificada");

            // Verifica que persistência foi realizada
            verify(userRepository).save(user);

            // Verifica todo o fluxo de processamento
            verify(userMapper).forUserEntity(userRequestDTO);
            verify(userMapper).forResponse(user);
            verify(utils).validateEmail(user.getEmail());
            verify(utils).validatePassword("MAPD95wd40_Z007@");
            verify(utils).validateName(user.getName());
            verify(passwordEncoder).encode("MAPD95wd40_Z007@");

            // Garante que não há interações extras
            verifyNoMoreInteractions(userRepository, userMapper, utils, passwordEncoder);
        }
    }

    /**
     * Testes para operação de atualização de senha de usuários.
     * Cobre validações de entrada, verificação de credenciais e persistência de nova senha.
     */
    @Nested
    class UpdatePasswordTests {

        private UserPasswordDTO userPasswordDTO;
        private User userFromDb;

        /**
         * Configuração específica para testes de atualização de senha.
         * Cria cenários isolados com dados controlados.
         */
        @BeforeEach
        void setUp() {
            // Configuração crítica: garante que o serviço pode obter o passwordEncoder via config
            // Esta é uma dependência arquitetural que deve ser mockada corretamente
            lenient().when(config.passwordEncoder()).thenReturn(passwordEncoder);

            // DTO com credenciais para teste de atualização
            userPasswordDTO = new UserPasswordDTO("test@example.com", "oldRawPassword", "newRawPassword");

            // Simula usuário existente no banco de dados com senha já codificada
            userFromDb = new User();
            userFromDb.setId(1L);
            userFromDb.setEmail("test@example.com");
            userFromDb.setPassword("encodedOldPasswordFromDB");
        }

        /**
         * CENÁRIO: Email vazio deve impedir qualquer processamento
         * EXPECTATIVA: Retorno falso sem interações com dependências
         */
        @Test
        @DisplayName("Deve retornar falso se o email estiver vazio")
        void shouldReturnFalse_whenEmailIsEmpty() {
            // Configura condição de falha: email vazio
            userPasswordDTO.setEmail("");

            // Executa operação
            boolean result = userServices.updatePassword(userPasswordDTO);

            // Verifica falha esperada
            assertFalse(result, "Deve retornar falso quando email está vazio");

            // VALIDAÇÃO CRÍTICA: confirma que validação inicial bloqueia processamento desnecessário
            // Isso demonstra que a lógica de saída antecipada está funcionando corretamente
            verifyNoInteractions(userRepository, passwordEncoder);
        }

        /**
         * CENÁRIO: Usuário não encontrado deve impedir atualização
         * EXPECTATIVA: Retorno falso após consulta ao banco, sem interação com encoder
         */
        @Test
        @DisplayName("Deve retornar falso se o usuário não for encontrado")
        void shouldReturnFalse_whenUserNotFound() {
            // Configura comportamento: usuário não existe no banco
            when(userRepository.findByEmail(userPasswordDTO.getEmail())).thenReturn(null);

            // Executa operação
            boolean result = userServices.updatePassword(userPasswordDTO);

            // Verifica falha esperada
            assertFalse(result, "Deve retornar falso quando usuário não existe");

            // Verifica que busca foi realizada
            verify(userRepository).findByEmail(userPasswordDTO.getEmail());
            verifyNoMoreInteractions(userRepository);

            // Confirma que não há processamento de senha sem usuário válido
            verifyNoInteractions(passwordEncoder);
        }

        /**
         * CENÁRIO: Senha atual incorreta deve impedir atualização
         * EXPECTATIVA: Retorno falso após verificação de credenciais, sem persistência
         */
        @Test
        @DisplayName("Deve retornar falso se a senha atual não corresponder")
        void shouldReturnFalse_whenCurrentPasswordMismatch() {
            // Configura comportamento: usuário encontrado, mas senha atual incorreta
            when(userRepository.findByEmail(userPasswordDTO.getEmail())).thenReturn(userFromDb);
            when(passwordEncoder.matches(userPasswordDTO.getPassword(), userFromDb.getPassword())).thenReturn(false);

            // Executa operação
            boolean result = userServices.updatePassword(userPasswordDTO);

            // Verifica falha esperada
            assertFalse(result, "Deve retornar falso quando senha atual está incorreta");

            // Verifica fluxo de autenticação
            verify(userRepository).findByEmail(userPasswordDTO.getEmail());
            verify(passwordEncoder).matches(userPasswordDTO.getPassword(), userFromDb.getPassword());

            // Confirma que usuário não foi persistido com credenciais inválidas
            verify(userRepository, never()).save(any());
        }

        /**
         * CENÁRIO: Nova senha igual à atual deve impedir atualização
         * EXPECTATIVA: Retorno falso após validação de duplicação, sem persistência
         */
        @Test
        @DisplayName("Deve retornar falso se a nova senha for igual à antiga")
        void shouldReturnFalse_whenNewPasswordIsSameAsOld() {
            // Configura comportamento: credenciais válidas, mas nova senha é idêntica à atual
            when(userRepository.findByEmail(userPasswordDTO.getEmail())).thenReturn(userFromDb);
            when(passwordEncoder.matches(userPasswordDTO.getPassword(), userFromDb.getPassword())).thenReturn(true);
            when(passwordEncoder.matches(userPasswordDTO.getNewPassword(), userFromDb.getPassword())).thenReturn(true);

            // Executa operação
            boolean result = userServices.updatePassword(userPasswordDTO);

            // Verifica falha esperada
            assertFalse(result, "Deve retornar falso quando nova senha é igual à atual");

            // Verifica busca de usuário
            verify(userRepository).findByEmail(userPasswordDTO.getEmail());

            // Confirma que ambas as verificações de senha foram realizadas (atual e nova)
            verify(passwordEncoder, times(2)).matches(anyString(), eq(userFromDb.getPassword()));

            // Garante que não houve persistência desnecessária
            verify(userRepository, never()).save(any());
        }

        /**
         * CENÁRIO: Dados válidos devem resultar em atualização bem-sucedida
         * EXPECTATIVA: Nova senha codificada e persistida, retorno verdadeiro
         * <p>
         * Este teste demonstra o fluxo completo de sucesso e utiliza técnicas avançadas
         * de verificação para validar tanto o estado final quanto o comportamento.
         */
        @Test
        @DisplayName("Deve atualizar a senha e retornar verdadeiro quando todos os dados são válidos")
        void shouldUpdatePasswordAndReturnTrue_whenAllInputsAreValid() {
            // === CONFIGURAÇÃO DO CENÁRIO DE SUCESSO ===
            String newEncodedPassword = "a-very-strong-new-encoded-password";

            // Usuário existe no banco de dados
            when(userRepository.findByEmail(userPasswordDTO.getEmail())).thenReturn(userFromDb);

            // Senha atual fornecida corresponde à armazenada
            when(passwordEncoder.matches(userPasswordDTO.getPassword(), userFromDb.getPassword())).thenReturn(true);

            // Nova senha é diferente da atual (validação de duplicação)
            when(passwordEncoder.matches(userPasswordDTO.getNewPassword(), userFromDb.getPassword())).thenReturn(false);

            // Nova senha é codificada com sucesso
            when(passwordEncoder.encode(userPasswordDTO.getNewPassword())).thenReturn(newEncodedPassword);

            // === EXECUÇÃO DA OPERAÇÃO ===
            boolean result = userServices.updatePassword(userPasswordDTO);

            // === VALIDAÇÕES DE RESULTADO ===

            // Verifica sucesso da operação
            assertTrue(result, "Deve retornar verdadeiro para atualização bem-sucedida");

            // TÉCNICA AVANÇADA: Verificação de estado usando ArgumentCaptor
            // Esta abordagem valida o resultado final (estado) em vez de apenas o comportamento,
            // tornando o teste mais robusto a mudanças de implementação
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            // Inspeciona o objeto que foi efetivamente salvo
            User savedUser = userCaptor.getValue();
            assertNotNull(savedUser, "Usuário salvo não deve ser nulo");
            assertEquals(newEncodedPassword, savedUser.getPassword(), "Senha salva deve ser a nova senha codificada");

            // === VALIDAÇÕES COMPORTAMENTAIS ESSENCIAIS ===

            // Confirma que busca foi realizada
            verify(userRepository).findByEmail(userPasswordDTO.getEmail());

            // ABORDAGEM PRAGMÁTICA: Verificação flexível das interações com passwordEncoder
            // Valida que as operações essenciais ocorreram sem ser excessivamente específico
            // sobre a ordem ou parâmetros exatos, aumentando a robustez do teste
            verify(passwordEncoder, atLeastOnce()).matches(anyString(), anyString());
            verify(passwordEncoder, times(1)).encode("newRawPassword");

            // OPÇÃO ALTERNATIVA (comentada): Verificação detalhada
            // Use apenas se precisar de controle rigoroso sobre cada interação
            /*
            verify(passwordEncoder).matches("oldRawPassword", "encodedOldPasswordFromDB");
            verify(passwordEncoder).matches("newRawPassword", "encodedOldPasswordFromDB");
            verify(passwordEncoder).encode("newRawPassword");
            */

            // Garante que não há interações não documentadas ou efeitos colaterais
            verifyNoMoreInteractions(userRepository, passwordEncoder);
        }
    }

    /**
     * Testes para operação de exclusão de usuários.
     * Valida autenticação de credenciais e remoção segura de dados.
     */
    @Nested
    class Delete {

        /**
         * CENÁRIO: Usuário não encontrado deve impedir exclusão
         * EXPECTATIVA: Retorno falso sem tentativa de remoção
         */
        @Test
        void shouldReturnFalseIfUserNotFound() {
            // Configura comportamento: usuário não existe
            when(userRepository.findByEmail(userDeleteDTO.getEmail())).thenReturn(null);

            // Executa operação
            boolean result = userServices.delete(userDeleteDTO);

            // Verifica falha esperada
            assertFalse(result, "Deve retornar falso quando usuário não existe");

            // Verifica que busca foi realizada
            verify(userRepository).findByEmail(userDeleteDTO.getEmail());

            // Confirma que não houve tentativa de exclusão
            verify(userRepository, never()).delete(any());
            verifyNoMoreInteractions(userRepository);

            // Garante que dependências de segurança não foram acionadas desnecessariamente
            verifyNoInteractions(userMapper, utils, config, passwordEncoder);
        }

        /**
         * CENÁRIO: Senha incorreta deve impedir exclusão
         * EXPECTATIVA: Retorno falso após verificação de credenciais falhada
         */
        @Test
        void shouldReturnFalseIfPasswordMismatch() {
            // Configura comportamento: usuário existe, mas senha incorreta
            when(userRepository.findByEmail(userDeleteDTO.getEmail())).thenReturn(user);
            when(passwordEncoder.matches(userDeleteDTO.getPassword(), user.getPassword())).thenReturn(false);

            // Executa operação
            boolean result = userServices.delete(userDeleteDTO);

            // Verifica falha esperada por credenciais inválidas
            assertFalse(result, "Deve retornar falso quando senha está incorreta");

            // Verifica fluxo de autenticação
            verify(userRepository).findByEmail(userDeleteDTO.getEmail());
            verify(passwordEncoder).matches(userDeleteDTO.getPassword(), user.getPassword());

            // Confirma que exclusão foi bloqueada por segurança
            verify(userRepository, never()).delete(any());
            verifyNoMoreInteractions(userRepository, passwordEncoder);

            // Garante que outras dependências não foram envolvidas
            verifyNoInteractions(userMapper, utils);
        }

        /**
         * CENÁRIO: Credenciais válidas devem resultar em exclusão bem-sucedida
         * EXPECTATIVA: Usuário removido do banco e retorno verdadeiro
         */
        @Test
        void shouldDeleteUserWhenCredentialsValid() {
            // Configura comportamento para exclusão autorizada
            when(userRepository.findByEmail(userDeleteDTO.getEmail())).thenReturn(user);
            when(passwordEncoder.matches(userDeleteDTO.getPassword(), user.getPassword())).thenReturn(true);

            // Executa operação
            boolean result = userServices.delete(userDeleteDTO);

            // Verifica sucesso da operação
            assertTrue(result, "Deve retornar verdadeiro quando credenciais são válidas");

            // Verifica fluxo completo: busca, autenticação e exclusão
            verify(userRepository).findByEmail(userDeleteDTO.getEmail());
            verify(passwordEncoder).matches(userDeleteDTO.getPassword(), user.getPassword());
            verify(userRepository).delete(user);

            // Confirma que não há interações extras
            verifyNoMoreInteractions(userRepository, passwordEncoder);

            // Garante que operação foi cirúrgica, sem envolver dependências desnecessárias
            verifyNoInteractions(userMapper, utils);
        }
    }
}