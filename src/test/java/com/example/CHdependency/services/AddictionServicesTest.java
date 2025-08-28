package com.example.CHdependency.services;

import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.dto.addiction.AddictionDTO;
import com.example.CHdependency.dto.addiction.DeleteAddictionDTO;
import com.example.CHdependency.entities.Addiction;
import com.example.CHdependency.entities.Goal; // Assumindo que Goal entity existe
import com.example.CHdependency.entities.User;
import com.example.CHdependency.enums.addiction.Addictions;
import com.example.CHdependency.repositories.AddictionRepository;
import com.example.CHdependency.repositories.GoalRepository;
import com.example.CHdependency.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given; // Usando BDDMockito para clareza [1]
import static org.mockito.BDDMockito.then; // Usando BDDMockito para clareza [1]
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AddictionServicesTest {

    @InjectMocks
    private AddictionServices addictionServices;

    @Mock
    private AddictionRepository addictionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository metaRepository; // Corresponde ao nome usado em AddictionServices

    @Mock
    private ConfigAuthentication config;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private Addiction addiction;
    private Goal goal; // Para o cenário de exclusão que envolve Goal
    private AddictionDTO addictionDTO;
    private DeleteAddictionDTO deleteAddictionDTO;

    @BeforeEach
    void setUp() throws Exception {
        // CORREÇÃO: Injeta manualmente 'config' em 'addictionServices' via reflexão.
        // Isso é necessário porque 'config' não é injetado via construtor em AddictionServices.
        // A melhor prática seria refatorar AddictionServices para usar injeção por construtor para ConfigAuthentication.
        java.lang.reflect.Field configField = AddictionServices.class.getDeclaredField("config");
        configField.setAccessible(true); // Torna o campo acessível, mesmo que seja privado
        configField.set(addictionServices, config); // Injeta o mock 'config' na instância 'addictionServices'

        // Stubbing do password encoder retornado pelo mock de config [1]
        lenient().when(config.passwordEncoder()).thenReturn(passwordEncoder);

        // Fixture comum para User
        user = new User();
        user.setId(1L); // Necessário para findByUserId
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword"); // Representa a senha *codificada* armazenada no banco de dados

        // Fixture comum para Addiction
        addiction = new Addiction();
        addiction.setId(10L); // ID para simular uma entidade existente
        addiction.setType(Addictions.COCAINE);
        addiction.setUser(user);

        // Fixture comum para Goal (para o método delete)
        goal = new Goal();
        goal.setId(20L); // ID para simular uma entidade existente
        goal.setName("Quit Smoking Goal");
        goal.setUser(user);

        // AddictionDTO para o método create
        addictionDTO = new AddictionDTO();
        addictionDTO.setEmail(user.getEmail());
        addictionDTO.setPassword("rawPassword"); // Representa a senha *bruta* fornecida pelo usuário
        addictionDTO.setType(Addictions.COCAINE);

        // DeleteAddictionDTO para o método delete
        deleteAddictionDTO = new DeleteAddictionDTO();
        deleteAddictionDTO.setEmail(user.getEmail());
        deleteAddictionDTO.setPassword("rawPassword");
    }

    @Nested
    class CreateAddiction {
        @Test
        void shouldReturnFalseIfUserNotFound() {
            // Given: O usuário não existe no repositório
            given(userRepository.findByEmail(addictionDTO.getEmail())).willReturn(null);

            // When: Tenta criar um vício
            boolean result = addictionServices.create(addictionDTO);

            // Then: A criação deve falhar, e nenhuma outra interação deve ocorrer [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(addictionDTO.getEmail());
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(addictionRepository, config); // Nenhuma verificação de senha se o usuário não for encontrado
        }

        @Test
        void shouldReturnFalseIfPasswordInvalid() {
            // Given: O usuário é encontrado, mas a senha fornecida não corresponde à armazenada
            given(userRepository.findByEmail(addictionDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(addictionDTO.getPassword(), user.getPassword())).willReturn(false);

            // When: Tenta criar um vício
            boolean result = addictionServices.create(addictionDTO);

            // Then: A criação deve falhar, e o vício não deve ser salvo [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(addictionDTO.getEmail());
            then(passwordEncoder).should().matches(addictionDTO.getPassword(), user.getPassword());
            verifyNoMoreInteractions(userRepository, passwordEncoder);
            verifyNoInteractions(addictionRepository); // Nenhuma criação de vício se a senha for inválida
        }

        @Test
        void shouldCreateAddictionWhenValid() {
            // Given: Usuário é encontrado, senha é válida
            given(userRepository.findByEmail(addictionDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(addictionDTO.getPassword(), user.getPassword())).willReturn(true);
            // addictionRepository.save() é void, então não precisa stubbar seu retorno

            // When: Tenta criar um vício
            boolean result = addictionServices.create(addictionDTO);

            // Then: O vício deve ser criado com sucesso, e o método save deve ser chamado [1]
            assertTrue(result);

            // Captura o objeto Addiction passado para save e verifica suas propriedades [2]
            ArgumentCaptor<Addiction> addictionCaptor = ArgumentCaptor.forClass(Addiction.class);
            then(addictionRepository).should().save(addictionCaptor.capture());

            Addiction capturedAddiction = addictionCaptor.getValue();
            assertNotNull(capturedAddiction);
            assertEquals(addictionDTO.getType(), capturedAddiction.getType());
            assertEquals(user, capturedAddiction.getUser());

            // Verifica as interações
            then(userRepository).should().findByEmail(addictionDTO.getEmail());
            then(passwordEncoder).should().matches(addictionDTO.getPassword(), user.getPassword());
            verifyNoMoreInteractions(userRepository, passwordEncoder, addictionRepository);
        }

        @Test
        void shouldCreateAddictionWhenTypeIsNull() {
            // Given: Usuário e senha são válidos, mas o tipo do vício é nulo
            addictionDTO.setType(null); // Define o tipo como nulo para este teste
            given(userRepository.findByEmail(addictionDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(addictionDTO.getPassword(), user.getPassword())).willReturn(true);

            // When: Tenta criar um vício
            boolean result = addictionServices.create(addictionDTO);

            // Then: A criação deve ser bem-sucedida, pois o serviço não valida explicitamente o campo 'type'.
            // Se o campo 'type' não puder ser nulo no banco de dados, isso causaria um erro de persistência.
            // Para um comportamento mais robusto, o serviço deveria ter uma validação como StringUtils.hasText(addictionDto.getType()).
            assertTrue(result);

            // Captura o objeto Addiction passado para save e verifica suas propriedades
            ArgumentCaptor<Addiction> addictionCaptor = ArgumentCaptor.forClass(Addiction.class);
            then(addictionRepository).should().save(addictionCaptor.capture());

            Addiction capturedAddiction = addictionCaptor.getValue();
            assertNotNull(capturedAddiction);
            assertNull(capturedAddiction.getType()); // Confirma que o tipo foi salvo como null
            assertEquals(user, capturedAddiction.getUser());

            then(userRepository).should().findByEmail(addictionDTO.getEmail());
            then(passwordEncoder).should().matches(addictionDTO.getPassword(), user.getPassword());
            verifyNoMoreInteractions(userRepository, passwordEncoder, addictionRepository);
        }
    }

    @Nested
    class DeleteAddiction {
        @Test
        void shouldReturnFalseIfUserNotFound() {
            // Given: O usuário não existe
            given(userRepository.findByEmail(deleteAddictionDTO.getEmail())).willReturn(null);

            // When: Tenta deletar um vício
            boolean result = addictionServices.delete(deleteAddictionDTO);

            // Then: A exclusão deve falhar, e nenhuma outra interação [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(deleteAddictionDTO.getEmail());
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(addictionRepository, metaRepository, config);
        }

        @Test
        void shouldReturnFalseIfPasswordInvalid() {
            // Given: O usuário é encontrado, mas a senha é inválida
            given(userRepository.findByEmail(deleteAddictionDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(deleteAddictionDTO.getPassword(), user.getPassword())).willReturn(false);

            // When: Tenta deletar um vício
            boolean result = addictionServices.delete(deleteAddictionDTO);

            // Then: A exclusão deve falhar, e nenhuma busca de vício/meta [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(deleteAddictionDTO.getEmail());
            then(passwordEncoder).should().matches(deleteAddictionDTO.getPassword(), user.getPassword());
            verifyNoMoreInteractions(userRepository, passwordEncoder);
            verifyNoInteractions(addictionRepository, metaRepository);
        }

        @Test
        void shouldReturnFalseIfAddictionNotFound() {
            // Given: Usuário e senha são válidos, mas o vício não é encontrado
            given(userRepository.findByEmail(deleteAddictionDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(deleteAddictionDTO.getPassword(), user.getPassword())).willReturn(true);
            given(addictionRepository.findByUserId(user.getId())).willReturn(null);

            // When: Tenta deletar um vício
            boolean result = addictionServices.delete(deleteAddictionDTO);

            // Then: A exclusão deve falhar, e nenhuma busca de meta [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(deleteAddictionDTO.getEmail());
            then(passwordEncoder).should().matches(deleteAddictionDTO.getPassword(), user.getPassword());
            then(addictionRepository).should().findByUserId(user.getId());
            verifyNoMoreInteractions(userRepository, passwordEncoder, addictionRepository);
            verifyNoInteractions(metaRepository);
        }

        @Test
        void shouldReturnFalseIfGoalNotFound() {
            // Given: Usuário, senha e vício são válidos, mas a meta não é encontrada
            given(userRepository.findByEmail(deleteAddictionDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(deleteAddictionDTO.getPassword(), user.getPassword())).willReturn(true);
            given(addictionRepository.findByUserId(user.getId())).willReturn(addiction);
            given(metaRepository.findByUserId(user.getId())).willReturn(null);

            // When: Tenta deletar um vício
            boolean result = addictionServices.delete(deleteAddictionDTO);

            // Then: A exclusão deve falhar [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(deleteAddictionDTO.getEmail());
            then(passwordEncoder).should().matches(deleteAddictionDTO.getPassword(), user.getPassword());
            then(addictionRepository).should().findByUserId(user.getId());
            then(metaRepository).should().findByUserId(user.getId());
            verifyNoMoreInteractions(userRepository, passwordEncoder, addictionRepository, metaRepository);
        }

        @Test
        void shouldDeleteAddictionAndGoalWhenValid() {
            // Given: Todas as entidades (usuário, vício, meta) são encontradas e as credenciais são válidas
            given(userRepository.findByEmail(deleteAddictionDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(deleteAddictionDTO.getPassword(), user.getPassword())).willReturn(true);
            given(addictionRepository.findByUserId(user.getId())).willReturn(addiction);
            given(metaRepository.findByUserId(user.getId())).willReturn(goal);
            // metaRepository.delete() e addictionRepository.delete() são void, não precisa stubbar

            // When: Tenta deletar um vício
            boolean result = addictionServices.delete(deleteAddictionDTO);

            // Then: A exclusão deve ser bem-sucedida, e ambos os métodos de exclusão devem ser chamados [1]
            assertTrue(result);
            then(userRepository).should().findByEmail(deleteAddictionDTO.getEmail());
            then(passwordEncoder).should().matches(deleteAddictionDTO.getPassword(), user.getPassword());
            then(addictionRepository).should().findByUserId(user.getId());
            then(metaRepository).should().findByUserId(user.getId());
            then(metaRepository).should().delete(goal);
            then(addictionRepository).should().delete(addiction);
            verifyNoMoreInteractions(userRepository, passwordEncoder, addictionRepository, metaRepository);
        }
    }
}