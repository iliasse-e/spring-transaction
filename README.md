# Spring Transaction

Spring Transaction est le module spécifique chargé de l’intégration des transactions. Il offre plusieurs avantages :

- Il fournit une abstraction au dessus des différentes solutions disponibles dans le monde Java pour les gestion des transactions. Spring Transaction définit l’interface ``TransactionManager`` pour unifier ses différentes solutions et propose des classes concrètes pour chacun d’entre-elles.

- Il se base sur la programmation orientée aspect (AOP) pour gérer la démarcation transactionnelles dans le code de notre application.

- Il permet une gestion déclarative des transactions.

## La transaction

La notion de transaction est récurrente dans les systèmes d’information. Par exemple, la plupart des SGBDR (Oracle, MySQL, PostGreSQL…) **intègrent un moteur de transaction**. Une transaction est définie par le respect de quatre propriétés désignées par l’acronyme ACID :

**Atomicité**

  La transaction garantit que l’ensemble des opérations qui la composent sont soit toutes réalisées avec succès soit aucune n’est conservée.

**Cohérence**

  La transaction garantit qu’elle fait passer le système d’un état valide vers un autre état valide.

**Isolation**

  Deux transactions sont isolées l’une de l’autre. C’est-à-dire que leur exécution simultanée produit le même résultat que si elles avaient été exécutées successivement.

**Durabilité**

  La transaction garantit qu’après son exécution, les modifications qu’elle a apportées au système sont conservées durablement.

Une transaction est définie par un début et une fin qui peut être soit une validation des modifications (commit), soit une annulation des modifications effectuées (rollback). On parle de **démarcation transactionnelle** pour désigner la portion de code qui doit s’exécuter dans le cadre d’une transaction.

## Spring Boot et la configuration automatique

La plupart des applications qui interagissent avec un SGBDR n’incorporent pas de moteur de gestion de transactions. Elles se contentent de déléguer cette gestion au moteur interne du SGBDR.

Néanmoins, il existe des systèmes d’information qui supportent les transactions. La gestion des transactions n’est donc que partiellement liée aux systèmes de bases de données. C’est pour cette raison qu’il existe un standard Java dédié à la gestion des transactions : ``JTA`` (*Java Transaction API*). 

Il s’agit de l’API officielle pour interagir avec un moteur transactionnel. Cependant, cette API n’est pas systématiquement utilisée et il existe des solutions fournies par des technologies particulières. Par exemple, JDBC et JPA fournissent toutes deux leur propre solution et leur propre API pour gérer les transactions impliquant spécifiquement des bases de données. Il existe donc plusieurs solutions pour implémenter la gestion des transactions en Java.

Heureusement, Spring Boot joue pleinement son rôle en rendant, la plupart du temps, la configuration de la gestion des transactions complètement transparente. Spring Boot va se baser sur les dépendances déclarées dans le projet pour savoir quel gestionnaire de transaction (``TransactionManager``) doit être créé dans le contexte de votre application.

Si votre projet est géré par Maven et que vous avez ajouté une dépendance à :

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jdbc</artifactId>
</dependency>
```

Alors, c’est un bean de type ``JdbcTransactionManager`` qui sera ajouté dans votre contexte d’application et c’est l’API JDBC qui sera utilisée pour gérer les transactions.

Si vous avez ajouté une dépendance à :

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

Alors, c’est un bean de type ``JpaTransactionManager`` qui sera ajouté dans votre contexte d’application et c’est l’API JPA qui sera utilisée pour gérer les transactions.

Pour activer le support des transactions gérées géré par ``JTA``, il faut ajouter un moteur transactionnel comme Atomikos. Cela se fait toujours par la déclaration d’une dépendance :

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jta-atomikos</artifactId>
</dependency>
```

## Transaction et couche de service

*La démarcation transactionnelle* désigne la portion de code au début de laquelle une transaction doit être commencée et à la fin de laquelle la transaction doit être validée (commit) ou annulée (rollback).
Dans une approche objet, la méthode est une bonne unité pour déclarer la démarcation d’une transaction : la transaction est démarrée à l’appel de la méthode et elle est validée (ou annulée) au retour de la méthode.

Dans une architecture multi-couches (N-tiers), on identifie généralement une couche de service, appelée aussi couche métier (business tier). Comme une méthode d’une classe de service représente la réalisation d’une fonctionnalité de l’application, il est assez évident que les méthodes de service sont de bonnes candidates pour fournir une démarcation transactionnelle.

> [!WARNING]
> Pour des raisons discutables, Spring Data JPA choisit d’activer par défaut les transactions sur les méthodes des repositories. Cela signifie que les transactions fonctionnent par défaut mais qu’elles sont validées par les méthodes des repositories, c’est-à-dire dans la couche d’accès aux données. Cela peut entraîner des incohérences de données. Par exemple, la méthode d’un service appelle plusieurs méthodes de repositories pour réaliser une fonctionnalité. Si un problème survient au cours de l’exécution de la méthode de service alors les appels déjà effectués aux repositories ne pourront pas être annulés.
>
> À part pour des applications très simples, la démarcation transactionnelle au niveau de la couche d’accès aux données est systématiquement une mauvaise idée. Si vous utilisez Spring Data JPA, vous devriez désactiver la prise en charge automatique des transactions par les repositories avec l’annotation ``@EnableJpaRepositories``
>
> 
>```java
>@SpringBootApplication
>@EnableJpaRepositories(enableDefaultTransactions = false)
>public class MyApplication {
>
>  public static void main(String[] args) {
>    SpringApplication.run(MyApplication.class, args);
>  }
>
>}
>```
>
> Une fois cette option désactivée, cela signifie qu’un appel à une méthode de repository qui effectue une modification sur la base de données devra être appelée dans le cadre d’une transaction ou sinon l’appel échouera.


## L’annotation @Transactional

Avec Spring Transaction, la démarcation transactionnelle est marquée par l’annotation ``@Transactional`` que l’on ajoute sur des méthodes.

> [!NOTICE]
> Il existe deux annotations ``@Transactional`` : celle fournie par le Spring Framework (``org.springframework.transaction.annotation.Transactional``) et celle fournie par JTA (``javax.transaction.Transactional``). Le Spring Framework est capable d’utiliser les deux. Elles permettent de configurer les transactions de la même manière mais leurs attributs diffèrent légèrement. Dans cette section, nous présenterons l’annotation ``@Transactional`` fournie par le Spring Framework.

```java
@Service
public class UserService {

  @Transactional(readOnly = true)
  public User getUser() {
    // ...
  }

  @Transactional
  public void saveUser(User user) {
    // ...
  }
}
```

L’annotation ``@Transactional`` supporte des propriétés afin de pouvoir configurer le support de transaction. Ainsi, l’attribut ``readOnly`` permet d’indiquer si la transaction est en lecture seule (``false`` par défaut).


> [!NOTICE]
> Pour les interactions avec les bases de données, les transactions en lecture seule signifient que l’on n’effectue que des requêtes pour lire des données.
>
> Une transaction en lecture seule est sensée être plus facile à gérer pour un moteur transactionnel et lui permettre d’effectuer des optimisations. Dans la pratique, si votre application se base sur le moteur transactionnel de votre SGBDR, il y a des chances pour que ce moteur ne fasse aucune différence entre une transaction et une transaction en lecture seule.

Pour les méthodes annotées avec ``@Transactional``, une transaction est démarrée à l’appel de cette méthode et est validée au retour de la méthode. Nous n’avons pas de code particulier à écrire pour cela. Spring Transaction utilise la programmation orientée aspect pour instrumenter notre code afin d’obtenir le comportement souhaité.


## Gestion déclarative du rollback

Par défaut, une transaction est invalidée (*rollback*) uniquement si la méthode transactionnelle échoue à cause d’une *unchecked* exception. Une *unchecked* exception est une exception qui n’est pas vérifiée par le compilateur (d’où son nom). Il s’agit des classes d’exception qui héritent de ``RuntimeException`` ou de ``Error``. Dans tous les autres cas, la transaction est validée (un *commit* est effectué).

Donc si une méthode se termine par une checked exception, Spring Transaction considère la transaction comme valide et réalise un *commit*. Cela peut paraître étonnant comme comportement par défaut mais c’est malheureusement celui qui a été choisi.

Si le comportement par défaut ne convient pas, il est possible d’utiliser l’attribut ``rollbackFor`` de l’annotation ``@Transactional`` pour ajouter une ou plusieurs classes d’exception qui devront produire un *rollback*.

```java
@Service
public class UserService {

  @Transactional(rollbackFor = UserExistsException.class)
  public void saveUser(User user) throws UserExistsException, NoEmailException {
    // ...
  }
}
```

Dans l’exemple ci-dessus, la méthode du service peut lever deux exceptions. ``UserExistsException`` entraînera un *rollback* comme spécifié dans l’annotation. Par contre, ``NoEmailException`` ne produira pas de *rollback* s’il s’agit d’une *checked* exception.

> [!NOTICE]
> Il existe également l’attribut ``noRollbackFor`` pour spécifier un ou des types d’exception pour lesquels on ne souhaite pas faire de rollback s’ils sont levés.
>
>La classe de l’exception donnée dans les attributs ``rollbackFor`` et ``noRollbackFor`` implique également toutes les classes qui héritent de cette exception. Ainsi si vous voulez être sûr qu’un rollback aura lieu pour n’importe quelle exception, vous pouvez écrire :
>
> 
>```java
>@Transactional(rollbackFor = Exception.class)
>public void executerService() throws ServiceException {
>  // ...
>}
>```
> Comme toutes les exceptions héritent directement ou indirectement de la classe ``Exception``, la levée de n’importe quelle exception produira un rollback dans la méthode ``executerService``.
