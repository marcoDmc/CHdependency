package com.example.CHdependency.services;

import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.dto.goal.DeleteGoalDTO;
import com.example.CHdependency.dto.goal.FindGoalPeriodDTO;
import com.example.CHdependency.dto.goal.GoalDTO;
import com.example.CHdependency.entities.Addiction; // Assumindo que Addiction entity existe
import com.example.CHdependency.entities.Goal;
import com.example.CHdependency.entities.User;
import com.example.CHdependency.repositories.AddictionRepository;
import com.example.CHdependency.repositories.GoalRepository;
import com.example.CHdependency.repositories.UserRepository;
import com.example.CHdependency.utils.Utils; // Importa a classe real Utils, não o mock
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils; // Adicionado para a explicação da correção no serviço

import java.time.Period;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given; // Usando BDDMockito para clareza [1]
import static org.mockito.BDDMockito.then; // Usando BDDMockito para clareza [1]
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class GoalServicesTest {

    @InjectMocks
    private GoalServices goalServices;

    @Mock
    private GoalRepository metaRepository; // Corresponde ao nome usado em GoalServices

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddictionRepository addictionRepository;

    // REMOVIDO: O campo 'utils' em GoalServices é inicializado diretamente como 'new Utils()'
    // e é declarado 'final'. Isso significa que o Mockito NÃO PODE injetar uma instância mock
    // de 'Utils' no objeto 'goalServices'. Portanto, removemos a anotação @Mock aqui
    // e permitimos que a instância real de Utils seja usada pelo serviço.
    // Se 'Utils' tivesse dependências externas ou lógica complexa que precisasse ser mockada,
    // 'GoalServices' precisaria ser refatorado para usar injeção por construtor para 'Utils'.
    // @Mock
    // private Utils utils;

    @Mock
    private ConfigAuthentication config;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Utils utils;

    private User user;
    private Goal goal;
    private GoalDTO goalDTO;
    private FindGoalPeriodDTO findGoalPeriodDTO;
    private DeleteGoalDTO deleteGoalDTO;
    private Addiction addiction;

    @BeforeEach
    void setUp() throws Exception { // Adicionado 'throws Exception' para lidar com reflexão
        // CORREÇÃO PARA NullPointerException: Injeta manualmente 'config' em 'goalServices'.
        // Esta é uma solução alternativa porque 'config' não é injetado via construtor em GoalServices,
        // e @InjectMocks parece falhar ao injetá-lo via injeção de campo nesta configuração específica.
        // A melhor prática seria refatorar GoalServices para usar injeção por construtor para ConfigAuthentication.
        java.lang.reflect.Field configField = GoalServices.class.getDeclaredField("config");
        configField.setAccessible(true); // Torna o campo acessível, mesmo que seja privado
        configField.set(goalServices, config); // Injeta o mock 'config' na instância 'goalServices'

        // NOTA IMPORTANTE: O campo 'utils' em GoalServices é 'final' e inicializado diretamente
        // como 'new Utils()'. Isso significa que o Mockito não pode injetar o mock 'utils'
        // desta classe de teste. O serviço usará sua própria instância 'new Utils()'.
        // Não precisamos de reflexão aqui para 'utils' porque não estamos tentando sobrescrever
        // uma instância 'final' já inicializada.
        // Se o campo 'utils' em GoalServices não fosse 'final' e não fosse inicializado,
        // @InjectMocks tentaria injetá-lo, ou poderíamos injetá-lo manualmente aqui.

        // Stubbing do password encoder retornado pelo mock de config [1]
        lenient().when(config.password()).thenReturn(passwordEncoder);

        // Fixture comum para User
        user = new User();
        user.setId(1L); // Necessário para findByUserId no método delete
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword"); // Representa a senha *codificada* armazenada no banco de dados

        // Fixture comum para Goal
        goal = new Goal();
        goal.setName("Test Goal");
        goal.setPeriod(Period.ofMonths(3)); // Exemplo de período para teste
        goal.setUser(user);

        // Fixture comum para Addiction (para o método delete)
        addiction = new Addiction();
        // Nenhuma propriedade específica é necessária para o mock de Addiction, apenas sua existência.

        // GoalDTO para o método create
        goalDTO = new GoalDTO();
        goalDTO.setEmail(user.getEmail());
        goalDTO.setPassword("rawPassword"); // Representa a senha *bruta* fornecida pelo usuário
        goalDTO.setName("Test Goal");
        goalDTO.setTime(3);
        goalDTO.setRange(com.example.CHdependency.enums.goal.Goal.MONTHS); // Assumindo Goal enum para range

        // FindGoalPeriodDTO para o método findPeriod
        findGoalPeriodDTO = new FindGoalPeriodDTO();
        findGoalPeriodDTO.setEmail(user.getEmail());
        findGoalPeriodDTO.setPassword("rawPassword");
        findGoalPeriodDTO.setName("Test Goal");

        // DeleteGoalDTO para o método delete
        deleteGoalDTO = new DeleteGoalDTO();
        deleteGoalDTO.setEmail(user.getEmail());
        deleteGoalDTO.setPassword("rawPassword");
        deleteGoalDTO.setName("Test Goal");
    }

    @Nested
    class CreateGoal {
        @Test
        void shouldReturnFalseIfUserNotFound() {
            // Given: O usuário não existe no repositório
            given(userRepository.findByEmail(goalDTO.getEmail())).willReturn(null);

            // When: Tenta criar uma meta
            boolean result = goalServices.create(goalDTO);

            // Then: A criação deve falhar, e nenhuma outra interação deve ocorrer [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(goalDTO.getEmail());
            verifyNoMoreInteractions(userRepository); // Garante que nenhuma outra chamada foi feita em userRepository [2]
            verifyNoInteractions(metaRepository, config); // Nenhuma verificação de senha se o usuário não for encontrado
        }

        @Test
        void shouldReturnFalseIfPasswordInvalid() {
            // Given: O usuário é encontrado, mas a senha fornecida não corresponde à armazenada
            given(userRepository.findByEmail(goalDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(goalDTO.getPassword(), user.getPassword())).willReturn(false);

            // When: Tenta criar uma meta
            boolean result = goalServices.create(goalDTO);

            // Then: A criação deve falhar, e a meta não deve ser salva [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(goalDTO.getEmail());
            then(passwordEncoder).should().matches(goalDTO.getPassword(), user.getPassword());
            verifyNoMoreInteractions(userRepository, passwordEncoder);
            verifyNoInteractions(metaRepository); // Nenhuma criação de meta se a senha for inválida
        }

        @Test
        void shouldReturnFalseIfGoalNameIsNull() {
            goalDTO.setName(null);

            // When: Attempt to create a goal
            boolean result = goalServices.create(goalDTO);

            // Then: Creation should fail, and no repository or encoder methods should be called
            assertFalse(result);
            verify(userRepository, never()).findByEmail(anyString());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(metaRepository, never()).save(any(Goal.class));
        }

        @Test
        void shouldCreateGoalWhenValid() {
            // Given: Usuário é encontrado, senha é válida
            given(userRepository.findByEmail(goalDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(goalDTO.getPassword(), user.getPassword())).willReturn(true);

            // When: Tenta criar uma meta
            boolean result = goalServices.create(goalDTO);

            // Then: A meta deve ser criada com sucesso, e o método save deve ser chamado [1]
            assertTrue(result);

            // Verifica as interações
            then(userRepository).should().findByEmail(goalDTO.getEmail());
            then(passwordEncoder).should().matches(goalDTO.getPassword(), user.getPassword());

            // Captura o objeto Goal passado para save e verifica suas propriedades [2]
            ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
            then(metaRepository).should().save(goalCaptor.capture());

            Goal capturedGoal = goalCaptor.getValue();
            assertNotNull(capturedGoal);
            assertEquals(goalDTO.getName(), capturedGoal.getName());
            assertEquals(user, capturedGoal.getUser());

            // IMPORTANTE: Como Utils não é mockado, o método real Utils.returnPeriod() é chamado.
            // Afirmamos o período com base na lógica real de Utils.
            Period expectedPeriod = new Utils().returnPeriod(goalDTO.getTime(), goalDTO.getRange());
            assertEquals(expectedPeriod, capturedGoal.getPeriod());

            verifyNoMoreInteractions(userRepository, passwordEncoder, metaRepository);
            // Não há verifyNoMoreInteractions para utils, pois é uma instância real
        }
    }

    @Nested
    class FindGoalPeriod {
        @Test
        void shouldReturnNullIfUserNotFound() {
            // Given: O usuário não existe
            given(userRepository.findByEmail(findGoalPeriodDTO.getEmail())).willReturn(null);

            // When: Tenta encontrar o período da meta
            Map<String, Object> result = goalServices.findPeriod(findGoalPeriodDTO);

            // Then: Deve retornar null, e nenhuma outra interação [1]
            assertNull(result);
            then(userRepository).should().findByEmail(findGoalPeriodDTO.getEmail());
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(metaRepository, config);
        }

        @Test
        void shouldReturnNullIfPasswordInvalid() {
            // Given: O usuário é encontrado, mas a senha é inválida
            given(userRepository.findByEmail(findGoalPeriodDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(findGoalPeriodDTO.getPassword(), user.getPassword())).willReturn(false);

            // When: Tenta encontrar o período da meta
            Map<String, Object> result = goalServices.findPeriod(findGoalPeriodDTO);

            // Then: Deve retornar null, e nenhuma busca de meta [1]
            assertNull(result);
            then(userRepository).should().findByEmail(findGoalPeriodDTO.getEmail());
            then(passwordEncoder).should().matches(findGoalPeriodDTO.getPassword(), user.getPassword());
            verifyNoMoreInteractions(userRepository, passwordEncoder);
            verifyNoInteractions(metaRepository);
        }

        @Test
        void shouldReturnNullIfGoalNotFound() {
            // Given: Usuário e senha são válidos, mas a meta não é encontrada
            given(userRepository.findByEmail(findGoalPeriodDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(findGoalPeriodDTO.getPassword(), user.getPassword())).willReturn(true);
            given(metaRepository.findByName(findGoalPeriodDTO.getName())).willReturn(null);

            // When: Tenta encontrar o período da meta
            Map<String, Object> result = goalServices.findPeriod(findGoalPeriodDTO);

            // Then: Deve retornar null [1]
            assertNull(result);
            then(userRepository).should().findByEmail(findGoalPeriodDTO.getEmail());
            then(passwordEncoder).should().matches(findGoalPeriodDTO.getPassword(), user.getPassword());
            then(metaRepository).should().findByName(findGoalPeriodDTO.getName());
            verifyNoMoreInteractions(userRepository, passwordEncoder, metaRepository);
        }

        @Test
        void shouldReturnPeriodWhenValid() {
            // Given: Usuário, senha e meta são todos válidos
            given(userRepository.findByEmail(findGoalPeriodDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(findGoalPeriodDTO.getPassword(), user.getPassword())).willReturn(true);
            given(metaRepository.findByName(findGoalPeriodDTO.getName())).willReturn(goal); // 'goal' tem Period.ofMonths(3)

            // When: Tenta encontrar o período da meta
            Map<String, Object> result = goalServices.findPeriod(findGoalPeriodDTO);

            // Then: Deve retornar um mapa com os detalhes corretos do período [1]
            assertNotNull(result);
            assertEquals(0, result.get("days")); // Period.ofMonths(3) tem 0 dias
            assertEquals(0, result.get("weeks")); // 3 meses / 4 semanas por mês = 0 semanas (divisão inteira)
            assertEquals(3, result.get("months"));
            then(userRepository).should().findByEmail(findGoalPeriodDTO.getEmail());
            then(passwordEncoder).should().matches(findGoalPeriodDTO.getPassword(), user.getPassword());
            then(metaRepository).should().findByName(findGoalPeriodDTO.getName());
            verifyNoMoreInteractions(userRepository, passwordEncoder, metaRepository);
        }
    }

    @Nested
    class DeleteGoal {
        @Test
        void shouldReturnFalseIfUserNotFound() {
            // Given: O usuário não existe
            given(userRepository.findByEmail(deleteGoalDTO.getEmail())).willReturn(null);

            // When: Tenta deletar uma meta
            boolean result = goalServices.delete(deleteGoalDTO);

            // Then: A exclusão deve falhar, e nenhuma outra interação [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(deleteGoalDTO.getEmail());
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(metaRepository, addictionRepository, config);
        }

        @Test
        void shouldReturnFalseIfPasswordInvalid() {
            // Given: O usuário é encontrado, mas a senha é inválida
            given(userRepository.findByEmail(deleteGoalDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(deleteGoalDTO.getPassword(), user.getPassword())).willReturn(false);

            // When: Tenta deletar uma meta
            boolean result = goalServices.delete(deleteGoalDTO);

            // Then: A exclusão deve falhar, e nenhuma busca de meta [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(deleteGoalDTO.getEmail());
            then(passwordEncoder).should().matches(deleteGoalDTO.getPassword(), user.getPassword());
            verifyNoMoreInteractions(userRepository, passwordEncoder);
            verifyNoInteractions(metaRepository, addictionRepository);
        }

        @Test
        void shouldReturnFalseIfGoalNotFound() {
            // Given: Usuário e senha são válidos, mas a meta não é encontrada
            given(userRepository.findByEmail(deleteGoalDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(deleteGoalDTO.getPassword(), user.getPassword())).willReturn(true);
            given(metaRepository.findByName(deleteGoalDTO.getName())).willReturn(null);

            // When: Tenta deletar uma meta
            boolean result = goalServices.delete(deleteGoalDTO);

            // Then: A exclusão deve falhar, e nenhuma busca de vício [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(deleteGoalDTO.getEmail());
            then(passwordEncoder).should().matches(deleteGoalDTO.getPassword(), user.getPassword());
            then(metaRepository).should().findByName(deleteGoalDTO.getName());
            verifyNoMoreInteractions(userRepository, passwordEncoder, metaRepository);
            verifyNoInteractions(addictionRepository);
        }

        @Test
        void shouldReturnFalseIfAddictionNotFound() {
            // Given: Usuário, senha e meta são válidos, mas o vício não é encontrado
            given(userRepository.findByEmail(deleteGoalDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(deleteGoalDTO.getPassword(), user.getPassword())).willReturn(true);
            given(metaRepository.findByName(deleteGoalDTO.getName())).willReturn(goal);
            given(addictionRepository.findByUserId(user.getId())).willReturn(null);

            // When: Tenta deletar uma meta
            boolean result = goalServices.delete(deleteGoalDTO);

            // Then: A exclusão deve falhar [1]
            assertFalse(result);
            then(userRepository).should().findByEmail(deleteGoalDTO.getEmail());
            then(passwordEncoder).should().matches(deleteGoalDTO.getPassword(), user.getPassword());
            then(metaRepository).should().findByName(deleteGoalDTO.getName());
            then(addictionRepository).should().findByUserId(user.getId());
            verifyNoMoreInteractions(userRepository, passwordEncoder, metaRepository, addictionRepository);
        }

        @Test
        void shouldDeleteGoalAndAddictionWhenValid() {
            // Given: Todas as entidades (usuário, meta, vício) são encontradas e as credenciais são válidas
            given(userRepository.findByEmail(deleteGoalDTO.getEmail())).willReturn(user);
            given(passwordEncoder.matches(deleteGoalDTO.getPassword(), user.getPassword())).willReturn(true);
            given(metaRepository.findByName(deleteGoalDTO.getName())).willReturn(goal);
            given(addictionRepository.findByUserId(user.getId())).willReturn(addiction);

            // When: Tenta deletar uma meta
            boolean result = goalServices.delete(deleteGoalDTO);

            // Then: A exclusão deve ser bem-sucedida, e ambos os métodos de exclusão devem ser chamados [1]
            assertTrue(result);
            then(userRepository).should().findByEmail(deleteGoalDTO.getEmail());
            then(passwordEncoder).should().matches(deleteGoalDTO.getPassword(), user.getPassword());
            then(metaRepository).should().findByName(deleteGoalDTO.getName());
            then(addictionRepository).should().findByUserId(user.getId());
            then(metaRepository).should().delete(goal);
            then(addictionRepository).should().delete(addiction);
            verifyNoMoreInteractions(userRepository, passwordEncoder, metaRepository, addictionRepository);
        }
    }
}